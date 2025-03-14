package guiProject;

import java.util.ArrayList;
import javax.swing.JOptionPane;

/*
주요 기능:
- 게임 보드의 각 칸 타입 정의
- 칸별 특수 효과 구현
- 이벤트 처리 로직
*/
public interface Stage {
	// 도착했을 때의 동작을 정의하는 메서드
	void 도착(int stageNum);
}

class EventStage implements Stage {
	private GameGUI gameGUI;

	public void setGameGUI(GameGUI gui) {
		this.gameGUI = gui;
	}

	@Override
	public void 도착(int stageNum) {
		// GUI에서 처리하므로 비워둠
	}
	
	public boolean solveQuiz(int location) {
		String difficulty;
		if (location > 0 && location <= 10) {
			difficulty = "EASY";
		} else if (location > 10 && location <= 20) {
			difficulty = "NORMAL";
		} else {
			difficulty = "HARD";
		}
		
		ArrayList<Quiz> selectedList = QuizManager.getQuizListDifficulty(difficulty);
		
		if (selectedList.isEmpty()) {
			if (gameGUI != null) {
				JOptionPane.showMessageDialog(gameGUI, "출제할 퀴즈가 없습니다!");
			}
			return false;
		}

		int randomIndex = (int) (Math.random() * selectedList.size());
		Quiz quiz = selectedList.get(randomIndex);

		if (gameGUI != null) {
			gameGUI.showQuizDialog(quiz);
			QuizManager.quizRemove(quiz, difficulty);
			return true;
		}
		return false;
	}
}

class ForceMove implements Stage {
	int forceStage;

	public ForceMove(int forceStage) {
		this.forceStage = forceStage;
	}

	public int getForceStage() {
		return forceStage;
	}
	
	// 도착하면 그 칸의 정보가 출력
	@Override
	public void 도착(int stageNum) {
		// GUI에서 처리하므로 비워둠
	}
}

class BuffStage implements Stage{
	String buff; // 이동거리 변화 효과

	// 생성자 이동거리 변화 효과를 받는다.
	public BuffStage(String buff) {
		this.buff = buff;
	}
	public BuffStage() {
		
	}
	

	// getter 이동거리 변화 효과
	public String getBuff() {
		return buff;
	}
	
	@Override
	public void 도착(int stageNum) {
		// GUI에서 처리하므로 비워둠
	}
}

//유령칸
class GhostStage implements Stage {

	@Override
	public void 도착(int stageNum) {
		// GUI에서 처리하므로 비워둠
	}
}

//일반칸
class NormalStage implements Stage {

	@Override
	public void 도착(int stageNum) {
		// GUI에서 처리하므로 비워둠
	}
}