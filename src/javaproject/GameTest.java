package javaproject;

import java.util.Scanner;

public class GameTest {
    public static void main(String[] args) {
        GameMaster game = new GameMaster();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 보드게임 테스트 시작 ===");
        game.printBoard();

        while (!game.checkGoal()) {
            System.out.println("\n엔터를 누르면 주사위를 굴립니다...");
            scanner.nextLine();

            int dice = game.diceRoll();
            
            System.out.println("주사위: " + dice);

            game.userMove(dice);
            game.printBoard();
            game.nowLocation();
        }

        System.out.println("\n=== 게임 클리어! ===");
        scanner.close();
    }
}
