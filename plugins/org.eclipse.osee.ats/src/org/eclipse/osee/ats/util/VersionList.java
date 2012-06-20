package org.eclipse.osee.ats.util;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.ats.core.model.IAtsVersion;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XListViewer;

public class VersionList extends XListViewer {

   public VersionList(String displayLabel) {
      super(displayLabel);
      setLabelProvider(new AtsObjectLabelProvider());
      setContentProvider(new ArrayContentProvider());
   }

   public Collection<IAtsVersion> getSelectedAtsObjects() {
      return Collections.castMatching(IAtsVersion.class, getSelected());
   }

   public void setInputAtsObjects(Collection<? extends IAtsVersion> arts) {
      ArrayList<Object> objs = new ArrayList<Object>();
      objs.addAll(arts);
      setInput(objs);
   }

}