package javaproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class QuizManager {
	private ArrayList<Quiz> easy = new ArrayList<>(); // easy 문제 리스트
	private ArrayList<Quiz> normal = new ArrayList<>(); // normal 문제 리스트
	private ArrayList<Quiz> hard = new ArrayList<>(); // hard 문제 리스트

	// 문제들 불러오기
	public QuizManager() {
		try {
			loadQuizFile("res/EASY.txt", "EASY"); // easy 문제 불러오기
			loadQuizFile("res/NORMAL.txt", "NORMAL"); // normal 문제 불러오기
			loadQuizFile("res/HARD.txt", "HARD"); // hard 문제 불러오기
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 문제 파일 읽기
	private void loadQuizFile(String fileName, String difficulty) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName)); // 버퍼 리더
		String line; // 텍스트 파일에 문제 라인 저장 변수

		// 각 문제를 배열에 저장
		while ((line = br.readLine()) != null) {
			String[] parts = line.split("&&"); // &&에 따라 분류

			// 데이터 형식이 올바른지 확인
			if (parts.length < 3) {
				continue; // 건너뛰기
			}

			String content = parts[0].trim(); // 문제
			String example = parts[1].trim(); // 보기
			String answer = parts[2].trim(); // 답

			Quiz quiz = new Quiz(content, example, answer);
			addToListDifficulty(difficulty, quiz);
		}
	}

	// 난이도별 리스트에 문제 추가
	private void addToListDifficulty(String difficulty, Quiz quiz) {
		switch (difficulty) {
		case "EASY":
			easy.add(quiz);
			break;
		case "NORMAL":
			normal.add(quiz);
			break;
		case "HARD":
			hard.add(quiz);
			break;
		default:
			System.out.println("알 수 없는 난이도: " + difficulty);
		}
	}

	// 문제풀이 실패하면 false 성공하면 true
	public boolean answerCheck(int location) {
		Scanner sc = new Scanner(System.in);
		int timeLimit; // 제한시간 변수

		// 도착한 이벤트 칸의 난이도 판별
		String difficulty;
		if (location > 0 && location <= 10) {
			difficulty = "EASY";
			timeLimit = 1000 * 60; // easy 제한시간 : 1분
		} else if (location > 10 && location <= 20) {
			difficulty = "NORMAL";
			timeLimit = 1000 * 180; // normal 제한시간 : 3분
		} else {
			difficulty = "HARD";
			timeLimit = 1000 * 300; // hard 제한시간 : 5분
		}

		// 난이도에 맞는 문제 리스트 선택
		ArrayList<Quiz> selectedList = getQuizListDifficulty(difficulty);

		// 배열에 문제가 없을 경우
		if (selectedList.isEmpty()) {
			System.out.println("출제할 문제가 없습니다!");
			return false;
		}

		// 랜덤 문제 선택 (리스트 크기 내에서 인덱스 선택)
		int randomIndex = (int) (Math.random() * selectedList.size());
		Quiz quiz = selectedList.get(randomIndex);

		QuizTimer timer = new QuizTimer(); // QuizTimer 가져옴
		timer.startTimer(timeLimit); // 제한 시간 셋팅

		// 문제 출력
		System.out.println(quiz.getContent());
		System.out.println(quiz.getExample());

		// 정답 입력
		System.out.print("\n정답을 입력하세요: ");

		String userAnswer = null; // 사용자의 답변을 저장할 변수
		while (0 < timeLimit) { // 제한시간 내에
			if (sc.hasNextLine()) { // 사용자가 입력했을 경우
				userAnswer = sc.nextLine(); // 답변 저장
				break;
			} else if (timer.isTimeUp()) { // 시간이 초과되었을 경우
				sc.close(); // Scanner 객체 닫기
				break;
			}
		}

		timer.cancelTimer(); // 타이머 정지

		// 시간 초과 시 자동 실패 처리
		if (timer.isTimeUp()) {
			System.out.println("\n시간 초과!");
			quizRemove(quiz, difficulty); // 해당 문제 배열에서 삭제
			return false;
		}

		// 정답 체크
		if (quiz.isCorrect(userAnswer)) {
			quizRemove(quiz, difficulty); // 리스트에서 문제 제거
			return true;
		} else {
			quizRemove(quiz, difficulty); // 리스트에서 문제 제거
			return false;
		}
	}

	// 리스트의 난이도 판별
	private ArrayList<Quiz> getQuizListDifficulty(String difficulty) {
		switch (difficulty) {
		case "EASY":
			return easy;
		case "NORMAL":
			return normal;
		case "HARD":
			return hard;
		default:
			return new ArrayList<>();
		}
	}

	// 풀었던 문제 없애는 메서드
	private void quizRemove(Quiz quiz, String difficulty) {
		switch (difficulty) {
		case "EASY":
			easy.remove(quiz);
			break;
		case "NORMAL":
			normal.remove(quiz);
			break;
		case "HARD":
			hard.remove(quiz);
			break;
		default:
			System.out.println("알 수 없는 난이도: " + difficulty);
		}
	}
}
