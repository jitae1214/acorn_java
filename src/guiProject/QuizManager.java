package guiProject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class QuizManager {
    static ArrayList<Quiz> easy = new ArrayList<>(); //easy 문제 리스트
    static ArrayList<Quiz> normal = new ArrayList<>(); //normal 문제 리스트
    static ArrayList<Quiz> hard = new ArrayList<>(); //hard 문제 리스트

    // 문제들 불러오기
    static void quizSetting() throws IOException {
    	loadQuizFile("res/EASY.txt", "EASY"); //easy 문제 불러오기
        loadQuizFile("res/NORMAL.txt", "NORMAL"); //normal 문제 불러오기
        loadQuizFile("res/HARD.txt", "HARD"); //hard 문제 불러오기
    }

    // 문제 파일 읽기
    static void loadQuizFile(String fileName, String difficulty) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName)); //버퍼 리더
		String line; //텍스트 파일에 문제 라인 저장 변수
		
		//각 문제를 배열에 저장
		while((line = br.readLine()) != null) {
			String[] parts = line.split("&&"); //&&에 따라 분류
			
			// 데이터 형식이 올바른지 확인
			if (parts.length < 3) {
				continue; // 건너뛰기
			}
			
			String content = parts[0].trim(); //문제
			String example = parts[1].trim(); //보기
			String answer = parts[2].trim(); //답
			
			Quiz quiz = new Quiz(content, example, answer);
			addToListDifficulty(difficulty, quiz);
		}
		br.close();
	}

    //난이도별 리스트에 문제 추가
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
        }
    }
    
    // 문제풀이 실패하면 false 성공하면 true
	static boolean answerCheck(int location) {
		//도착한 이벤트 칸의 난이도 판별
		String difficulty;
	    if (location > 0 && location <= 10) {
	        difficulty = "EASY";
	    } else if (location > 10 && location <= 20) {
	        difficulty = "NORMAL";
	    } else {
	        difficulty = "HARD";
	    }
	    
	    //난이도에 맞는 문제 리스트 선택
	    ArrayList<Quiz> selectedList = getQuizListDifficulty(difficulty);
	    
	    //배열에 문제가 없을 경우
	    if (selectedList.isEmpty()) {
	        System.out.println("출제할 문제가 없습니다!");
	        return false;
	    }

	    // 랜덤 문제 선택 (리스트 크기 내에서 인덱스 선택)
	    int randomIndex = (int) (Math.random() * selectedList.size());
	    Quiz quiz = selectedList.get(randomIndex);

	    // 문제 출력
	    System.out.println(quiz.getContent());
	    System.out.println(quiz.getExample());
	    
	    //정답 입력
	    String userAnswer = System.console().readLine();

	    // 정답 체크
	    if (quiz.isCorrect(userAnswer)) {
	        quizRemove(quiz, difficulty);  //리스트에서 문제 제거
	        return true;
	    } else {
	        quizRemove(quiz, difficulty);  //리스트에서 문제 제거
	        return false;
	    }
	}
	
	//리스트의 난이도 판별
	public static ArrayList<Quiz> getQuizListDifficulty(String difficulty) {
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

	//풀었던 문제 없애는 메서드
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
