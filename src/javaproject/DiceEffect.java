package javaproject;

/*
public class DiceEffect {
    try {
        String[] diceFrames = {
                """
            +-------+
            |       |
            |   •   |
            |       |
            +-------+""",  // ⚀
                """
            +-------+
            | •     |
            |       |
            |     • |
            +-------+""",  // ⚁
                """
            +-------+
            | •     |
            |   •   |
            |     • |
            +-------+""",  // ⚂
                """
            +-------+
            | •   • |
            |       |
            | •   • |
            +-------+""",  // ⚃
                """
            +-------+
            | •   • |
            |   •   |
            | •   • |
            +-------+""",  // ⚄
                """
            +-------+
            | •   • |
            | •   • |
            | •   • |
            +-------+"""   // ⚅
        };

        String[] colors = {
                "\u001B[31m", // 빨강
                "\u001B[32m", // 초록
                "\u001B[33m", // 노랑
                "\u001B[34m", // 파랑
                "\u001B[35m", // 보라
                "\u001B[36m"  // 청록
        };

        System.out.println("\n주사위를 굴립니다...");

        // 빠른 회전 효과 (10회)
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 6; j++) {
                System.out.print("\033[H\033[2J");  // 화면 클리어
                System.out.flush();
                System.out.println(colors[j] + diceFrames[j] + "\u001B[0m");
                Thread.sleep(50);
            }
        }

        // 최종 결과
        int result = random.nextInt(6) + 1;
        System.out.print("\033[H\033[2J");
        System.out.flush();

        // 결과 강조 효과
        for (int i = 0; i < 3; i++) {
            System.out.println("\u001B[1m" + diceFrames[result-1] + "\u001B[0m");
            Thread.sleep(200);
            System.out.print("\033[H\033[2J");
            System.out.flush();
            Thread.sleep(200);
        }

        // 최종 결과 표시
        System.out.println("\u001B[1;33m" + diceFrames[result-1] + "\u001B[0m");
        System.out.println("\n주사위 결과: " + result);

        return result;

    } catch (InterruptedException e) {
        System.out.println("주사위 굴리기 중단");
        return random.nextInt(6) + 1;
    }
}
}
*/
