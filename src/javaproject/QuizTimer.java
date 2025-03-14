package javaproject;

import java.util.Timer;
import java.util.TimerTask;

public class QuizTimer {
	Timer timer;
	boolean timeUp;
	
	public QuizTimer() {
		this.timer = new Timer();
		this.timeUp = false;
	}
	
	//타이머 시작
	public void startTimer(int timeLimit) {
		timer.schedule(new TimerTask() {
			public void run() {
				timeUp = true;
			}
		}, timeLimit);
	}
	
	//타이머 취소
	public void cancelTimer() {
		timer.cancel();
	}
	
	//시간이 초과 되었는지 확인
	public boolean isTimeUp() {
		return timeUp;
	}
	
}
