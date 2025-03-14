package javaproject;
/*
주요 기능:
- 게임 보드의 각 칸 타입 정의
- 칸별 특수 효과 구현
- 이벤트 처리 로직
*/
public interface Stage {
	// 도착하면 그 칸의 정보가 출력되게 하는 추상 메서드
	void 도착(int stageNum);
}

class EventStage implements Stage{
	// 도착하면 그 칸의 정보가 출력
	@Override
	public void 도착(int stageNum) {
		System.out.println("현재 위치 : " + stageNum + "칸");
		System.out.println("현재 칸 : [E]-이벤트\n");
	}
	
	// 현재 위치를 받아 문제 풀이 하는 메서드
	// 문제 풀이 성공하면 true, 실패하면 false 반환
	public boolean solveQuiz(int location, QuizManager quizManager) {
		return quizManager.answerCheck(location);
	}
	
}

class ForceMove implements Stage {
	int forceStage; // 강제 이동할 칸수

	// 생성자 강제 이동할 칸수를 받는다
	public ForceMove(int forceStage) {
		this.forceStage = forceStage;
	}

	// getter 강제 이동할 칸수
	public int getForceStage() {
		return forceStage;
	}
	
	// 도착하면 그 칸의 정보가 출력
	@Override
	public void 도착(int stageNum) {
		
		System.out.println("현재 칸 : [F]-강제이동");
	}
}

class BuffStage implements Stage{
	String buff; // 이동거리 변화 효과

	// 생성자 이동거리 변화 효과를 받는다.
	public BuffStage(String buff) {
		this.buff = buff;
	}
	public BuffStage() {
		 this.buff = "normal";
	}
	

	// getter 이동거리 변화 효과
	public String getBuff() {
		return buff;
	}
	
	@Override
	public void 도착(int stageNum) {
		    System.out.println("현재 위치 : " + stageNum + "칸");
	        System.out.println("현재 칸 : [B]-아이템");
	        System.out.println("버프 효과 획득: " + buff);
	}
}

//유령칸
class GhostStage implements Stage {

	@Override
	public void 도착(int stageNum) {
	
		System.out.println("현재 칸 : [G]-유령");
	}
}

//일반칸
class NormalStage implements Stage {

	@Override
	public void 도착(int stageNum) {
		
		System.out.println("현재 칸 : [N]-일반");
	}
}