/**
 * 
 */
package org.eclipse.osee.ats.editor;

import java.util.List;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;

/**
 * @author Jeff C. Phillips
 */
public class AtsTaskRenderer extends DefaultArtifactRenderer {
   private static final String COMMAND_ID = "org.eclipse.osee.framework.ui.skynet.atstaskeditor.command";
   public static final String RENDERER_EXTENSION = "org.eclipse.osee.ats.editor.AtsTaskRenderer";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#open(java.util.List)
    */
   @Override
   public void open(List<Artifact> artifacts) throws OseeCoreException {
      if (OseeAts.getAtsLib() != null) {
         OseeAts.getAtsLib().openInAtsTaskEditor("Tasks", artifacts);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#preview(java.util.List)
    */
   @Override
   public void preview(List<Artifact> artifacts) throws OseeCoreException {
      open(artifacts);
   }

   /**
    * @param rendererId
    */
   public AtsTaskRenderer(String rendererId) {
      super(rendererId);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#getName()
    */
   @Override
   public String getName() {
      return "ATS Task Editor";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#getId()
    */
   @Override
   public String getId() {
      return RENDERER_EXTENSION;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#getApplicabilityRating(org.eclipse.osee.framework.ui.skynet.render.PresentationType, org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) throws OseeCoreException {
      if (artifact instanceof TaskArtifact) {
         return PRESENTATION_SUBTYPE_MATCH;
      }
      return NO_MATCH;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer#getCommandId()
    */
   @Override
   public String getCommandId() {
      return COMMAND_ID;
   }

   @Override
   public AtsTaskRenderer newInstance() throws OseeCoreException {
      return new AtsTaskRenderer(getId());
   }

}
