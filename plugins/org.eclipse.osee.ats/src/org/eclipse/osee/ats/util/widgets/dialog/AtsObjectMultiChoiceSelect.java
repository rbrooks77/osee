package org.eclipse.osee.ats.util.widgets.dialog;

import java.util.Collections;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.core.model.IAtsObject;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.MinMaxOSEECheckedFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.SimpleCheckFilteredTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.XSelectFromDialog;

/**
 * @author Donald G. Dunne
 */
public class AtsObjectMultiChoiceSelect extends XSelectFromDialog<IAtsObject> {

   public static final String WIDGET_ID = AtsObjectMultiChoiceSelect.class.getSimpleName();

   public AtsObjectMultiChoiceSelect() {
      super("Select Artifact (s)");
      setSelectableItems(Collections.<IAtsObject> emptyList());
   }

   @Override
   public MinMaxOSEECheckedFilteredTreeDialog createDialog() {
      SimpleCheckFilteredTreeDialog dialog =
         new SimpleCheckFilteredTreeDialog(getLabel(), "Select from the items below", new ArrayTreeContentProvider(),
            new LabelProvider(), new AtsObjectNameSorter(), 1, 1000);
      return dialog;
   }

}
