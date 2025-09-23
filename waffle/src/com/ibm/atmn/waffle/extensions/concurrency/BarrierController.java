package com.ibm.atmn.waffle.extensions.concurrency;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Helper class for using cyclic barriers and countdown latches for concurrency testing.
 * 
 * @author Ruairi Pidgeon/Ireland/IBM
 *
 */
public final class BarrierController extends CyclicBarrier{

	private static long timeoutInMillis;
	private static int parties;
	private static Runnable barrierAction;
	
	private BarrierController(int parties, Runnable barrierAction) {

		super(parties, barrierAction);
	}

	private static class SingleBarrierControllerHolder{

		private static BarrierController oneBarrier = new BarrierController(parties, barrierAction);
	}

	public static BarrierController getInstance(int parties, long timeoutInMillis) {

		return getInstance(parties, timeoutInMillis, null);
	}
	
	public static BarrierController getInstance(int parties, long timeoutInMillis, Runnable barrierAction) {	
		BarrierController.parties = parties;
		BarrierController.timeoutInMillis = timeoutInMillis;
		BarrierController.barrierAction = barrierAction;
		return SingleBarrierControllerHolder.oneBarrier;
	}
	
	@Override
	public int await() {
		int index;
		try {
			//System.out.println("TEST INFO: Thread " + Thread.currentThread().getName() + " has arrived at the barrier.");
			index = await(timeoutInMillis, TimeUnit.MILLISECONDS);
			//System.out.println("TEST INFO: Thread " + (parties - index) + " of " + parties + " arrived at the barrier.");
			return index;
		} catch (InterruptedException e) {
			System.out.println("---------\nTEST ERROR: Barrier Breach: " + e + "\n---------");
			return 0;
		} catch (BrokenBarrierException e) {
			System.out.println("---------\nTEST ERROR: Barrier Breach: " + e + "\n---------");
			return 0;
		} catch (TimeoutException e) {
			System.out.println("---------\nTEST ERROR: Barrier Breach: " + e + "\n---------");
			return 0;
		}
	}
	
	public CountDownLatch getLatch(){
		CountDownLatch latch = new CountDownLatch(parties);
		//System.out.println("TEST INFO: Latch retrieved");
		return latch;
	}

	public void resetAndWaitOnLatch(CountDownLatch latch) {

		boolean nonRunners = false;
		reset();
		latch.countDown();
		//System.out.println("TEST INFO: Latch count down by thread: " + Thread.currentThread().getName());
		try {
			nonRunners = latch.await(timeoutInMillis*5, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("TEST INFO: Latch count down reached 0: " + nonRunners + ": by thread: " + Thread.currentThread().getName());
		
	}

}
