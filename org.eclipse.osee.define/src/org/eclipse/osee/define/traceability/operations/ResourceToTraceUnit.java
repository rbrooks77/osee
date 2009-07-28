/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.traceability.operations;

import java.net.URI;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.define.traceability.ITraceParser;
import org.eclipse.osee.define.traceability.ITraceUnitResourceLocator;
import org.eclipse.osee.define.traceability.data.TraceMark;
import org.eclipse.osee.define.traceability.data.TraceUnit;
import org.eclipse.osee.define.utility.IResourceHandler;
import org.eclipse.osee.define.utility.UriResourceContentFinder;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ResourceToTraceUnit {

   private final UriResourceContentFinder resourceFinder;
   private final Set<ITraceUnitProcessor> traceProcessors;
   private final HashCollection<ITraceUnitResourceLocator, ITraceParser> traceUnitHandlers;

   public ResourceToTraceUnit(final URI source, final boolean isRecursionAllowed, final boolean isFileWithMultiplePaths) {
      super();
      this.traceUnitHandlers = new HashCollection<ITraceUnitResourceLocator, ITraceParser>();
      this.traceProcessors = Collections.synchronizedSet(new HashSet<ITraceUnitProcessor>());
      this.resourceFinder = new UriResourceContentFinder(source, isRecursionAllowed, isFileWithMultiplePaths);
   }

   public void addTraceProcessor(ITraceUnitProcessor traceProcessor) {
      synchronized (traceProcessors) {
         traceProcessors.add(traceProcessor);
      }
   }

   public void removeTraceProcessor(ITraceUnitProcessor traceProcessor) {
      synchronized (traceProcessors) {
         traceProcessors.remove(traceProcessor);
      }
   }

   public void addTraceUnitHandler(ITraceUnitResourceLocator locator, ITraceParser parser) {
      traceUnitHandlers.put(locator, parser);
   }

   public void removeTestUnitLocator(ITraceUnitResourceLocator locator) {
      traceUnitHandlers.removeValues(locator);
   }

   private void clear() {
      for (ITraceUnitProcessor traceProcessor : traceProcessors) {
         traceProcessor.clear();
      }
      for (ITraceUnitResourceLocator locator : traceUnitHandlers.keySet()) {
         resourceFinder.removeLocator(locator);
      }
      System.gc();
   }

   public void execute(IProgressMonitor monitor) throws OseeCoreException {
      List<TraceUnitCollector> collectors = new ArrayList<TraceUnitCollector>();
      try {
         for (ITraceUnitResourceLocator locator : traceUnitHandlers.keySet()) {
            Collection<ITraceParser> parsers = traceUnitHandlers.getValues(locator);
            for (ITraceParser parser : parsers) {
               TraceUnitCollector testUnitCollector = new TraceUnitCollector(locator, parser);
               resourceFinder.addLocator(locator, testUnitCollector);
               collectors.add(testUnitCollector);
            }
         }

         final int TOTAL_WORK = Integer.MAX_VALUE;
         final int QUARTER_TOTAL = TOTAL_WORK / 4;

         SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, QUARTER_TOTAL);
         resourceFinder.execute(subMonitor);

         if (!monitor.isCanceled()) {
            subMonitor = new SubProgressMonitor(monitor, QUARTER_TOTAL);
            notifyOnInitialize(subMonitor);
         }

         if (!monitor.isCanceled()) {
            subMonitor = new SubProgressMonitor(monitor, QUARTER_TOTAL);
            subMonitor.beginTask("Processing", collectors.size());
            for (TraceUnitCollector collector : collectors) {
               if (monitor.isCanceled()) break;
               if (!collector.isEmpty()) {
                  processCollector(subMonitor, collector);
               }
               subMonitor.worked(1);
            }
            subMonitor.done();
         }

         if (!monitor.isCanceled()) {
            subMonitor = new SubProgressMonitor(monitor, QUARTER_TOTAL);
            notifyOnComplete(subMonitor);
         }
      } finally {
         collectors.clear();
         clear();
      }
   }

   private void processCollector(IProgressMonitor monitor, TraceUnitCollector testUnitCollector) throws OseeCoreException {
      for (String testUnitType : testUnitCollector.getTraceUnitTypes()) {
         if (monitor.isCanceled()) break;
         Map<String, TraceUnit> unitToTrace = testUnitCollector.getUnitsToTraceMarks(testUnitType);
         if (unitToTrace != null) {
            for (String testUnitName : unitToTrace.keySet()) {
               monitor.subTask(String.format("Processing [%s - %s]", testUnitType, testUnitName));
               if (monitor.isCanceled()) break;
               TraceUnit testUnit = unitToTrace.get(testUnitName);
               if (testUnit != null) {
                  notifyOnProcess(monitor, testUnit);
               }
            }
         }
      }
   }

   private void notifyOnProcess(IProgressMonitor monitor, TraceUnit testUnit) throws OseeCoreException {
      for (ITraceUnitProcessor traceProcessor : traceProcessors) {
         traceProcessor.process(monitor, testUnit);
      }
   }

   private void notifyOnInitialize(IProgressMonitor monitor) {
      monitor.beginTask("Initialize", traceProcessors.size());
      for (ITraceUnitProcessor traceProcessor : traceProcessors) {
         monitor.subTask(String.format("Initializing [%s]", traceProcessor.getClass().getSimpleName()));
         traceProcessor.initialize(monitor);
         monitor.worked(1);
      }
   }

   private void notifyOnComplete(IProgressMonitor monitor) throws OseeCoreException {
      monitor.beginTask("On Completion", traceProcessors.size());
      for (ITraceUnitProcessor traceProcessor : traceProcessors) {
         monitor.subTask(String.format("Completing [%s]", traceProcessor.getClass().getSimpleName()));
         traceProcessor.onComplete(monitor);
         monitor.worked(1);
      }
   }

   private final class TraceUnitCollector implements IResourceHandler {

      private final ITraceParser traceParser;
      private final ITraceUnitResourceLocator traceUnitLocator;
      private final Map<String, Map<String, TraceUnit>> traceUnitToTraceMarks;

      public TraceUnitCollector(ITraceUnitResourceLocator traceUnitLocator, ITraceParser traceParser) {
         this.traceParser = traceParser;
         this.traceUnitLocator = traceUnitLocator;
         this.traceUnitToTraceMarks = new HashMap<String, Map<String, TraceUnit>>();
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.define.traceability.IResourceHandler#onResourceFound(java.net.URI, java.lang.String, java.nio.CharBuffer)
       */
      @Override
      public void onResourceFound(URI uriPath, String name, CharBuffer fileBuffer) {
         String traceUnitType = traceUnitLocator.getTraceUnitType(name, fileBuffer);
         if (Strings.isValid(traceUnitType) && !traceUnitType.equalsIgnoreCase(ITraceUnitResourceLocator.UNIT_TYPE_UNKNOWN)) {
            Collection<TraceMark> traceMarks = traceParser.getTraceMarks(fileBuffer);
            if (traceMarks != null && !traceMarks.isEmpty()) {
               Map<String, TraceUnit> traceUnits = traceUnitToTraceMarks.get(traceUnitType);
               if (traceUnits == null) {
                  traceUnits = new HashMap<String, TraceUnit>();
                  traceUnitToTraceMarks.put(traceUnitType, traceUnits);
               }
               TraceUnit unit = traceUnits.get(name);
               if (unit == null) {
                  unit = new TraceUnit(traceUnitType, name);
                  traceUnits.put(name, unit);
               }
               unit.addAllTraceMarks(traceMarks);
            }
         }
      }

      public boolean isEmpty() {
         return traceUnitToTraceMarks.isEmpty();
      }

      public void reset() {
         this.traceUnitToTraceMarks.clear();
      }

      public Set<String> getTraceUnitTypes() {
         return traceUnitToTraceMarks.keySet();
      }

      public Map<String, TraceUnit> getUnitsToTraceMarks(String unitType) {
         return traceUnitToTraceMarks.get(unitType);
      }
   }
}
