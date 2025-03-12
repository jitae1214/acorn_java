package javaproject;

import java.util.Scanner;

public class GameTest {
    public static void main(String[] args) {
        GameMaster game = new GameMaster();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 보드게임 테스트 시작 ===");
        game.printBoard();

        // 골 체크는 게임마스터 안에서 확인하고 메인메서드에서는 게임 마스터 실행만 하게 변경 필요할 듯 합니당
        // 게임마스터 안에 run 메서드를 만들어서 그거 실행하면 할 수 있게
        while (!game.checkGoal()) {
            System.out.println("\n엔터를 누르면 주사위를 굴립니다...");
            scanner.nextLine();

            int dice = game.diceRoll();
            System.out.println("주사위: " + dice);

            game.userMove(dice);
            game.printBoard();
        }

        System.out.println("\n=== 게임 클리어! ===");
        scanner.close();
    }
}
