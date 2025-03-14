package day20;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class 타이머시간초과 {
    private static boolean timeUp = false;

    public static void main(String[] args) {
        // 제한 시간 설정 (예: 5초)
        int limitTime = 5 * 1000; // 5초 (밀리초 단위)

        // Timer를 사용하여 제한 시간 후 동작 정의
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeUp = true; // 시간이 다 되면 true로 설정
                System.out.println("제한 시간이 초과되었습니다!");
            }
        }, limitTime); // 5초 후 실행

        // 사용자 입력 받기
        Scanner scanner = new Scanner(System.in);
        System.out.println("제한 시간 내에 답을 입력하세요: ");
        
        String answer = "";
        while (!timeUp) {
            // 사용자가 입력을 완료할 때까지 기다림
            if (scanner.hasNextLine()) {
                answer = scanner.nextLine();
                System.out.println("입력한 답: " + answer);
                timer.cancel(); // 제한 시간이 끝나기 전에 입력이 완료되면 타이머를 취소
                break;
            }
        }
    }
}
