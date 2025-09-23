package com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.threads;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnActionRunner implements Runnable {

	private Queue<ConnAction> _queue = new ConcurrentLinkedQueue<ConnAction>();

	private boolean _run = true;

	private ConnActionRunnerListener _listener = null;

	public ConnActionRunner(ConnActionRunnerListener listener) {
		_listener = listener;

	}

	private String prefix() {
		return "thread(" + Thread.currentThread().getName() + "): ";
	}

	public void addToQueue(ConnAction action) {
		_queue.add(action);
	}

	public void run() {
		System.out.println(prefix() + "running");
		int counter = 0;
		while (_run || !_queue.isEmpty()) {
			ConnAction action = _queue.poll();
			if (action == null) {
				continue;
			}
			System.out.println(prefix() + "found action: " + action);
			try {
				action.doAction();
				counter++;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				if (_queue.isEmpty()) {
					Thread.sleep(10);
				}
			} catch (InterruptedException e) {
			}
		}
		System.out.println(prefix() + "Thread stopped. counter = " + counter);
		_listener.done(counter);

	}

	public void stop() {
		_run = false;
	}

	public static interface ConnActionRunnerListener {
		public void done(int counter);
	}

}
