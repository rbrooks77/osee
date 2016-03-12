package org.eclipse.ote.scheduler;

import java.util.concurrent.Callable;

public class OTETask implements Callable<OTETaskResult>, Comparable<OTETask>{

   private long time = 0;
   private Runnable r;
   private OTETaskResult result;
   private int period;
   private final boolean isScheduled;
   private volatile boolean complete = false;
   private boolean canceled;
   private boolean isMainThread = false;
   
   public OTETask(Runnable runnable, int period){
      isScheduled = true;
      this.r = runnable;
      this.period = period;
      result = new OTETaskResult();
   }
   
   public OTETask(Runnable runnable, long time) {
      isScheduled = false;
      this.time = time;
      this.r = runnable;
      this.period = 0;
      result = new OTETaskResult();
   }

   public long getTime() {
      return time;
   }

   @Override
   public OTETaskResult call() throws Exception {
      long startTime = System.nanoTime();
      result.th = null;
      try{
         if(!canceled){
            r.run();
         }
      } catch (Throwable th){
         result.th = th;
      } 
      result.elapsedTime = System.nanoTime() - startTime;
      complete  = true;
      return result;
   }

   public boolean isComplete(){
      return complete;
   }
   
   public boolean isScheduled() {
      return isScheduled;
   }

   public long period() {
      return period;
   }
   
   public void cancel(){
      canceled = true;
   }

   public void setNextTime(long l) {
      time = l;      
   }

   @Override
   public int compareTo(OTETask o) {
      long diff = time - o.time;
      if(diff > 0){
         return 1;
      } else if(diff == 0){
         return 0;
      } else {
         return -1;
      }
   }

   void setCanceled() {
      canceled = true;
   }
   
   public String toString(){
      return String.format("%d %s", period, r.toString());
   }

   public void setMain(boolean isMainThread) {
      this.isMainThread = isMainThread;
   }
   
   public boolean isMainThread(){
      return isMainThread;
   }
}
