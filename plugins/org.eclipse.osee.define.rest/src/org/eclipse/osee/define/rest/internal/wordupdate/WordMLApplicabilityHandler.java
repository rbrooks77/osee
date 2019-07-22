/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.internal.wordupdate;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.BranchView;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.FeatureDefinition;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GeneralStringData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.regex.Matcher;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock.ApplicabilityType;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarLexer;
import org.eclipse.osee.framework.core.grammar.ApplicabilityGrammarParser;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Ryan D. Brooks
 */
public class WordMLApplicabilityHandler {

   private static String SCRIPT_ENGINE_NAME = "JavaScript";

   private final Set<String> validConfigurations;
   private Map<String, List<String>> viewApplicabilitiesMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
   private final String configurationToView;
   private final Stack<ApplicabilityBlock> applicBlocks;
   private final String featureDefinitionJson;
   private final ScriptEngine se;
   private final Log logger;

   public WordMLApplicabilityHandler(OrcsApi orcsApi, Log logger, BranchId branch, ArtifactId view) {
      this.applicBlocks = new Stack<>();
      this.logger = logger;

      ScriptEngineManager sem = new ScriptEngineManager();
      se = sem.getEngineByName(SCRIPT_ENGINE_NAME);

      QueryFactory query = orcsApi.getQueryFactory();

      branch = getProductLineBranch(query, branch);
      validConfigurations = getValidConfigurations(query, branch);

      viewApplicabilitiesMap = query.applicabilityQuery().getNamedViewApplicabilityMap(branch, view);
      ArtifactToken viewArtifact = query.fromBranch(branch).andId(view).asArtifactToken();
      configurationToView = viewArtifact.getName();

      ArtifactReadable featureDefArt =
         query.fromBranch(branch).andTypeEquals(FeatureDefinition).getResults().getExactlyOne();
      featureDefinitionJson = featureDefArt.getSoleAttributeAsString(GeneralStringData);
   }

   public static BranchId getProductLineBranch(QueryFactory query, BranchId branch) {
      Branch br = query.branchQuery().andId(branch).getResults().getExactlyOne();
      if (br.getBranchType().equals(BranchType.MERGE)) {
         branch = br.getParentBranch();
      }
      return branch;
   }

   public String previewValidApplicabilityContent(String content) {
      String toReturn = content;
      int searchIndex = 0;
      int applicBlockCount = 0;

      Matcher matcher = WordCoreUtil.FULL_PATTERN.matcher(toReturn);

      while (searchIndex < toReturn.length() && matcher.find(searchIndex)) {
         String beginFeature = matcher.group(1);
         String beginConfig = matcher.group(26);

         String endFeature = matcher.group(12) != null ? WordCoreUtil.textOnly(matcher.group(12)).toLowerCase() : null;
         String endConfig = matcher.group(48) != null ? WordCoreUtil.textOnly(matcher.group(48)).toLowerCase() : null;

         if (beginFeature != null && WordCoreUtil.textOnly(beginFeature).toLowerCase().contains(
            WordCoreUtil.FEATUREAPP)) {
            applicBlockCount += 1;
            searchIndex =
               addApplicabilityBlock(ApplicabilityType.Feature, matcher, beginFeature, searchIndex, toReturn);

         } else if (beginConfig != null && WordCoreUtil.textOnly(beginConfig).toLowerCase().contains(
            WordCoreUtil.CONFIGAPP)) {
            if (isValidConfigurationBracket(beginConfig)) {
               applicBlockCount += 1;
               ApplicabilityType type = ApplicabilityType.Configuration;
               if (beginConfig.contains("Not")) {
                  type = ApplicabilityType.NotConfiguration;
               }
               searchIndex = addApplicabilityBlock(type, matcher, beginConfig, searchIndex, toReturn);
            } else {
               searchIndex = matcher.end();
            }

         } else if (endFeature != null && endFeature.contains(
            WordCoreUtil.FEATUREAPP) || endConfig != null && endConfig.contains(WordCoreUtil.CONFIGAPP)) {

            ApplicabilityBlock applicabilityBlock = getFullApplicabilityBlock(matcher, toReturn);

            if (applicabilityBlock == null) {
               searchIndex = matcher.end();
            } else {
               applicBlockCount -= 1;
               String toInsert = evaluateApplicabilityBlock(applicabilityBlock, toReturn);
               String toReplace =
                  toReturn.substring(applicabilityBlock.getStartInsertIndex(), applicabilityBlock.getEndInsertIndex());
               toReturn = toReturn.replace(toReplace, toInsert);
               searchIndex =
                  applicabilityBlock.getStartInsertIndex() + (applicabilityBlock.isInTable() ? 0 : toInsert.length());
               matcher = WordCoreUtil.FULL_PATTERN.matcher(toReturn);
            }
         } else {
            break;
         }
      }

      toReturn = removeEmptyLists(toReturn);
      if (applicBlockCount != 0) {
         logger.error("An applicability block of text is missing an End Feature/Configuration tag");
      }

      return toReturn;
   }

   private boolean isValidConfigurationBracket(String beginConfig) {
      beginConfig = WordCoreUtil.textOnly(beginConfig);
      int start = beginConfig.indexOf("[") + 1;
      int end = beginConfig.indexOf("]");
      String applicExpText = beginConfig.substring(start, end);

      String[] configs = applicExpText.split("&|\\|");
      for (int i = 0; i < configs.length; i++) {
         configs[i] = configs[i].split("=")[0].trim();
         if (!containsIgnoreCase(validConfigurations, configs[i])) {
            return false;
         }
      }

      return true;
   }

   // End Bracket can contain multiple feature/value pairs
   private boolean isValidFeatureBracket(String optionalEndBracket) {
      String text = WordCoreUtil.textOnly(optionalEndBracket);
      text = text.replaceAll("\\[", "");
      text = text.replaceAll("\\]", "").trim();

      // Split on ORs and ANDs
      String[] featureValueStrings = text.split("\\||&");
      for (String featureValueString : featureValueStrings) {
         String[] split = featureValueString.split("=");
         String featName = split[0].trim().toUpperCase();
         String featVal = split.length > 1 ? split[1].trim() : null;

         if (viewApplicabilitiesMap.containsKey(featName)) {
            List<String> values = viewApplicabilitiesMap.get(featName);
            if (featVal != null && !containsIgnoreCase(values, featVal)) {
               return false;
            }
         } else {
            return false;
         }
      }

      return true;
   }

   private String evaluateApplicabilityBlock(ApplicabilityBlock applicabilityBlock, String fullWordML) {
      Map<String, String> binDataMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
      saveBinData(applicabilityBlock.getFullText(), binDataMap);

      String toInsert = evaluateApplicabilityExpression(applicabilityBlock);
      return insertMissingbinData(toInsert, binDataMap);
   }

   private String removeEmptyLists(String wordML) {
      return wordML.replaceAll(WordCoreUtil.EMPTY_LIST_REGEX, "");
   }

   private String insertMissingbinData(String toInsert, Map<String, String> binDataMap) {
      String temp = toInsert;
      Matcher matcher = WordCoreUtil.IMG_SRC_PATTERN.matcher(temp);
      while (matcher.find()) {
         String srcId = matcher.group(1);
         if (binDataMap.containsKey(srcId)) {
            String binData = binDataMap.get(srcId);
            if (!temp.contains(binData)) {
               temp = binData + temp;
            }
         }
      }

      return temp;
   }

   private void saveBinData(String fullText, Map<String, String> binDataMap) {
      Matcher matcher = WordCoreUtil.BIN_DATA_PATTERN.matcher(fullText);
      while (matcher.find()) {
         binDataMap.put(matcher.group(1), matcher.group(0));
      }
   }

   private int addApplicabilityBlock(ApplicabilityType type, Matcher matcher, String applicabilityExpression, int searchIndex, String fullWordMl) {
      ApplicabilityBlock beginApplic = new ApplicabilityBlock();
      beginApplic.setType(type);
      //Remove extra space
      String applicExpText = WordCoreUtil.textOnly(applicabilityExpression).toLowerCase().replace(" [", "[");
      beginApplic.setApplicabilityExpression(applicExpText);
      beginApplic.setStartInsertIndex(matcher.start());
      beginApplic.setStartTextIndex(matcher.end());
      applicBlocks.push(beginApplic);
      searchIndex = matcher.end();

      return searchIndex;
   }

   private ApplicabilityBlock getFullApplicabilityBlock(Matcher matcher, String toReturn) {
      if (applicBlocks.isEmpty()) {
         logger.error("An applicability block of text is missing a start Feature/Configuration tag");
         return null;
      }
      ApplicabilityBlock applic = applicBlocks.pop();

      // set end insert index - Check if End Bracket is valid
      String optionalEndBracket = null;
      boolean isValidBracket = false;
      if (applic.getType().equals(ApplicabilityType.Configuration)) {
         int endBracketGroup = 65;
         optionalEndBracket = matcher.group(endBracketGroup);
         isValidBracket = optionalEndBracket == null ? false : isValidConfigurationBracket(optionalEndBracket);
      } else {
         int endBracketGroup = 23;
         optionalEndBracket = matcher.group(endBracketGroup);
         isValidBracket = optionalEndBracket == null ? false : isValidFeatureBracket(optionalEndBracket);
      }

      if (optionalEndBracket != null && !isValidBracket) {
         int newEndInsertIndex = matcher.end() - optionalEndBracket.length();
         applic.setEndInsertIndex(newEndInsertIndex);
      } else {
         applic.setEndInsertIndex(matcher.end());
      }
      applic.setEndTextIndex(matcher.start());

      String insideText = toReturn.substring(applic.getStartTextIndex(), applic.getEndTextIndex());
      applic.setFullText(insideText);

      // Adjust start and end insert indicies if tags are inside a table
      if (!applic.getFullText().contains(WordCoreUtil.TABLE) && applic.getFullText().contains(
         WordCoreUtil.TABLE_CELL)) {
         String findStartOfRow = toReturn.substring(0, applic.getStartInsertIndex());
         int startRowIndex = findStartOfRow.lastIndexOf(WordCoreUtil.START_TABLE_ROW);

         if (startRowIndex != -1) {
            // find end of row after the END configuration/feature tag
            String findEndOfRow = toReturn.substring(matcher.end());
            int endRowIndex = findEndOfRow.indexOf(WordCoreUtil.END_TABLE_ROW);
            if (endRowIndex != -1) {
               endRowIndex = endRowIndex + matcher.end() + 7;
               String fullText = toReturn.substring(startRowIndex, endRowIndex);
               applic.setIsInTable(true);
               applic.setStartInsertIndex(startRowIndex);
               applic.setEndInsertIndex(startRowIndex + fullText.length());

               fullText =
                  fullText.replaceFirst("(?i)(" + WordCoreUtil.ENDFEATURE + "|" + WordCoreUtil.ENDCONFIG + ")", "");
               fullText =
                  fullText.replaceFirst("(?i)(" + WordCoreUtil.BEGINFEATURE + "|" + WordCoreUtil.BEGINCONFIG + ")", "");
               applic.setFullText(fullText);
            }
         }
      }

      return applic;
   }

   private String evaluateApplicabilityExpression(ApplicabilityBlock applic) {
      String applicabilityExpression = applic.getApplicabilityExpression();
      String toInsert = "";
      try {

         String fullText = applic.getFullText();

         ApplicabilityGrammarLexer lex =
            new ApplicabilityGrammarLexer(new ANTLRStringStream(applicabilityExpression.toUpperCase()));
         ApplicabilityGrammarParser parser = new ApplicabilityGrammarParser(new CommonTokenStream(lex));

         parser.start();

         ApplicabilityType type = applic.getType();

         if (type.equals(ApplicabilityType.Feature)) {
            toInsert =
               getValidFeatureContent(fullText, applic.isInTable(), parser.getIdValuesMap(), parser.getOperators());
         } else if (type.equals(ApplicabilityType.Configuration) || type.equals(ApplicabilityType.NotConfiguration)) {
            toInsert = getValidConfigurationContent(type, fullText, parser.getIdValuesMap());
         }

      } catch (RecognitionException ex) {
         logger.error(
            "Failed to parse expression: " + applicabilityExpression + " at start Index: " + applic.getStartInsertIndex());
      }

      return toInsert;
   }

   public String getValidConfigurationContent(ApplicabilityType type, String fullText, HashMap<String, List<String>> id_value_map) {
      Matcher match = WordCoreUtil.ELSE_PATTERN.matcher(fullText);
      String beginningText = fullText;
      String elseText = "";

      if (match.find()) {
         beginningText = fullText.substring(0, match.start());

         elseText = fullText.substring(match.end());
         elseText = elseText.replaceAll(WordCoreUtil.ENDCONFIG, "");
         elseText = elseText.replaceAll(WordCoreUtil.BEGINCONFIG, "");
      }

      String toReturn = "";

      // Note: this assumes only OR's are put in between configurations
      List<String> values = id_value_map.get(configurationToView.toUpperCase());

      if (type.equals(ApplicabilityType.NotConfiguration)) {
         if (values != null) {
            toReturn = elseText;
         } else {
            toReturn = beginningText;
         }
      } else if (values == null) {
         toReturn = elseText;
      } else {
         toReturn = beginningText;
      }

      return toReturn;
   }

   private String getValidFeatureContent(String fullText, boolean isInTable, HashMap<String, List<String>> featureIdValuesMap, ArrayList<String> featureOperators) {

      Matcher match = WordCoreUtil.ELSE_PATTERN.matcher(fullText);
      String beginningText = fullText;
      String elseText = "";

      if (match.find()) {

         if (isInTable) {
            String temp = fullText.substring(0, match.end());
            // Find last occurence of table row
            int lastIndexOf = temp.lastIndexOf(WordCoreUtil.START_TABLE_ROW);
            if (lastIndexOf != -1) {
               elseText = fullText.substring(lastIndexOf);
               elseText = elseText.replaceAll(WordCoreUtil.ELSE_EXP, "");
               beginningText = fullText.substring(0, lastIndexOf);
            }
         } else {
            beginningText = fullText.substring(0, match.start());
            elseText = fullText.substring(match.end());
         }

         elseText = elseText.replaceAll(WordCoreUtil.ENDFEATURE, "");
         elseText = elseText.replaceAll(WordCoreUtil.BEGINFEATURE, "");
      }

      String toReturn = "";
      String expression = createFeatureExpression(featureIdValuesMap, featureOperators);

      boolean result = false;
      try {
         result = (boolean) se.eval(expression);
      } catch (ScriptException ex) {
         logger.error("Failed to parse expression: " + expression);
      }

      if (result) {
         toReturn = beginningText;
      } else {
         toReturn = elseText;
      }

      return toReturn;
   }

   private String createFeatureExpression(HashMap<String, List<String>> featureIdValuesMap, ArrayList<String> featureOperators) {

      String myFeatureExpression = "";
      Iterator<String> iterator = featureOperators.iterator();

      for (String feature : featureIdValuesMap.keySet()) {
         List<String> values = featureIdValuesMap.get(feature);

         String valueExpression = createValueExpression(feature, values);

         boolean result = false;

         try {
            result = (boolean) se.eval(valueExpression);
         } catch (ScriptException ex) {
            logger.error("Failed to parse expression: " + valueExpression);
         }

         myFeatureExpression += result + " ";

         if (iterator.hasNext()) {
            String next = iterator.next();
            if (next.equals("|")) {
               myFeatureExpression += "|| ";
            } else if (next.equals("&")) {
               myFeatureExpression += "&& ";
            }
         }
      }

      return myFeatureExpression;
   }

   private String createValueExpression(String feature, List<String> values) {
      String myValueExpression = "";
      for (String value : values) {
         if (value.equals("(")) {
            myValueExpression += "( ";
         } else if (value.equals(")")) {
            myValueExpression += ") ";
         } else if (value.equals("|")) {
            myValueExpression += "|| ";
         } else if (value.equals("&")) {
            myValueExpression += "&& ";
         } else {
            boolean eval = isFeatureValuePairValid(feature, value);
            myValueExpression += eval + " ";
         }
      }

      return myValueExpression;
   }

   private boolean isFeatureValuePairValid(String feature, String value) {
      if (viewApplicabilitiesMap.containsKey(feature)) {
         Collection<String> validValues = viewApplicabilitiesMap.get(feature);

         value = value.equalsIgnoreCase("Default") ? getDefaultValue(feature) : value.trim();

         if (containsIgnoreCase(validValues, value)) {
            return true;
         }
      }

      return false;
   }

   private boolean containsIgnoreCase(Collection<String> validValues, String val) {
      for (String validValue : validValues) {
         if (validValue.equalsIgnoreCase(val)) {
            return true;
         }
      }
      return false;
   }

   private String getDefaultValue(String feature) {
      String toReturn = null;
      FeatureDefinition[] featDataList = JsonUtil.readValue(featureDefinitionJson, FeatureDefinition[].class);

      for (FeatureDefinition featData : featDataList) {
         if (featData.getName().equalsIgnoreCase(feature)) {
            toReturn = featData.getDefaultValue();
            break;
         }
      }
      return toReturn;
   }

   public static HashSet<String> getValidConfigurations(QueryFactory query, BranchId branch) {
      HashSet<String> validConfigurations = new HashSet<>();

      List<ArtifactToken> views = query.fromBranch(branch).andTypeEquals(BranchView).asArtifactTokens();
      for (ArtifactToken view : views) {
         validConfigurations.add(view.getName().toUpperCase());
      }
      return validConfigurations;
   }
}
