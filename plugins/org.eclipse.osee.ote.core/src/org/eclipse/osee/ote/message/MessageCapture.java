package org.eclipse.osee.ote.message;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;

/**
 * This class will capture messages contained within the given {@link MessageCaptureFilter}'s.  This can be used from scripts to accomplish more complicated analysis of data.
 * <pre> An example of using it:{@code  
 
      MessageCapture capture = MessageCapture.createMessageCapture();      
      capture.add(new {@link MessageCaptureFilter}(messageOneReader));

      capture.start();
      //do work here ...
      capture.stop();
      
      {@link MessageCaptureDataIterator} it = capture.getDataIterator();
      {@link MessageCaptureChecker} checker = new MessageCaptureChecker(it);
      checker.add(new {@link CheckEqualsCondition}<>(messageOneReader, messageOneReader.element1, SOME_ENUM.value1, 0, 60000));
      checker.add(new CheckEqualsCondition<>(messageOneReader, messageOneReader.element2, SOME_ENUM.value2, 0, 60000));
      checker.add(new {@link CheckPulseCondition}<>(messageOneReader, messageOneReader.element3, SOME_ENUM.state2,  SOME_ENUM.state3, 1, 0, 60000));
      checker.check();
      checker.close();
      checker.logToOutfile(this);
      checker.saveData(environment);
 }</pre>
 * @author Andrew M. Finkbeiner
 *
 */
public class MessageCapture {

   private Set<MessageCaptureFilter> filters;
   private File file;
   private BinaryMessageRecorder recorder;
   private ClassLocator classLocator;
   
   public static MessageCapture createMessageCapture() throws IOException{
      OTEApi oteApi = ServiceUtility.getService(OTEApi.class);
      return new MessageCapture(oteApi.getServerFolder().getTestDataFolder(), oteApi.getTestEnvironment().getTimerCtrl(), new BasicClassLocator(ExportClassLoader.getInstance()));
   }
   
   public MessageCapture(File outputFolder, ITimerControl timerControl, ClassLocator classLocator) throws IOException{
      filters = new HashSet<>();
      this.classLocator = classLocator;
      outputFolder.mkdirs();
      file = File.createTempFile("messageCapture", ".bmr", outputFolder);
      recorder = BinaryMessageRecorder.create(file, timerControl);
   }
   
   /**
    * 
    * @param filters
    */
   public void add(MessageCaptureFilter... filters){
      for(MessageCaptureFilter filter:filters){
         this.filters.add(filter);
      }
   }
   
   public void start() throws IOException{
      for(MessageCaptureFilter filter: filters){
         for(Message msg:filter.getMessages()){
            if(msg.getDefaultMessageData().getType().equals(msg.getMemType())){
               recorder.addMessage(msg, new BinaryRecorderFilterCallback(filter));
            } else {
               //do some stuff here 
               throw new IllegalStateException("have not implemented mapping support for recording yet");
            }
         }
      }
      recorder.start("capture");
   }
   
   public void stop(){
      recorder.close();
   }
   
   /**
    * 
    * @return a {@link MessageCaptureDataIterator} that can be used to analyze the OTEBinaryRecording that is being written to by this class.  If the 
    * capture has not been stopped there are no guarantees about how much of the data can be analyzed since it is dependent on when things are written
    * to the file.
    * 
    * @throws IOException
    */
   public MessageCaptureDataIterator getDataIterator() throws IOException {
      return new MessageCaptureDataIterator(recorder.getDestinationFile(), classLocator);
   }
      
}
