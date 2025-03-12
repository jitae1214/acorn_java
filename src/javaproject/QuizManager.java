package javaproject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class QuizManager {
    static ArrayList<Quiz> easy = new ArrayList<>();
    static ArrayList<Quiz> normal = new ArrayList<>();
    static ArrayList<Quiz> hard = new ArrayList<>();

    // 문제들 불러오기
    static void quizSetting() throws IOException {
    	loadQuizFile("res/EASY.txt", "EASY");
        loadQuizFile("res/NORMAL.txt", "NORMAL");
        loadQuizFile("res/HARD.txt", "HARD");
    }

    // 문제 파일 읽기
    static void loadQuizFile(String fileName, String difficulty) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line;
		
		while((line = br.readLine()) != null) {
			String[] parts = line.split("&&");
			
			// 데이터 형식이 올바른지 확인
			if (parts.length < 3) {
				continue; // 건너뛰기
			}
			
			String content = parts[0].trim();
			String example = parts[1].trim();
			String answer = parts[2].trim();
			
			Quiz quiz = new Quiz(content, example, answer);
			addToListDifficulty(difficulty, quiz);
		}
	}

    // 난이도별 리스트에 문제 추가
    static void addToListDifficulty(String difficulty, Quiz quiz) {
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
    
    static {
        try {
            quizSetting();
        } catch (IOException e) {
            System.out.println("문제를 불러오는 중 오류 발생: " + e.getMessage());
        }
    }

    // 문제 출력 메서드
    void printQuiz(ArrayList<Quiz> level, int index) {
        Quiz quiz = level.get(index);

        System.out.println(quiz.content);
        System.out.println(quiz.example);
        System.out.println(quiz.answer);
    }
    
    // 문제풀이 실패하면 false 성공하면 true
	static boolean answerCheck(int location) {
		Scanner scanner = new Scanner(System.in);
		
		String difficulty;
	    if (location > 0 && location < 10) {
	        difficulty = "EASY";
	    } else if (location > 10 && location < 20) {
	        difficulty = "NORMAL";
	    } else {
	        difficulty = "HARD";
	    }
	    
	 // 난이도에 맞는 문제 리스트 선택
	    ArrayList<Quiz> selectedList = getQuizListDifficulty(difficulty);
	    
	    if (selectedList.isEmpty()) {
	        System.out.println("출제할 문제가 없습니다!");
	        return false;
	    }
	    
	    // 랜덤 문제 선택
	    // 랜덤 문제 선택 (리스트 크기 내에서 인덱스 선택)
	    int randomIndex = (int) (Math.random() * selectedList.size());
	    Quiz quiz = selectedList.get(randomIndex);

	    // 문제 출력
	    System.out.println("문제: " + quiz.getContent());
	    System.out.println("보기: " + quiz.getExample());
	    System.out.print("정답을 입력하세요: ");
	    
	    String userAnswer = scanner.nextLine();

	    // 정답 체크
	    if (quiz.isCorrect(userAnswer)) {
	        System.out.println("정답입니다!");
	        quizRemove(quiz, difficulty);  // 문제 제거
	        return true;
	    } else {
	        System.out.println("틀렸습니다!");
	        quizRemove(quiz, difficulty);  // 문제 제거
	        return false;
	    }
	}
	
	private static ArrayList<Quiz> getQuizListDifficulty(String difficulty) {
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
	static void quizRemove(Quiz quiz, String difficulty) {
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
