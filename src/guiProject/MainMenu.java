package guiProject;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
/*
주요 기능:
- 게임의 시작 화면 구현
- 맵 선택, 설정, 제작자 정보 표시
- 유령 애니메이션 효과
- UI/UX 디자인
*/

public class MainMenu extends JFrame {
    // 메인 메뉴의 모든 버튼에 적용될 기본 너비 (픽셀 단위)
    private static final int BUTTON_WIDTH = 300;
    
    // 메인 메뉴의 모든 버튼에 적용될 기본 높이 (픽셀 단위)
    private static final int BUTTON_HEIGHT = 60;
    
    // 유령 페이드 인/아웃 효과를 제어하는 타이머
    private Timer ghostAnimationTimer;

    // 유령의 현재 투명도 값 (0~255)
    private int ghostAlpha = 0;

    // 유령이 나타나는 중인지(true) 사라지는 중인지(false) 나타내는 상태 값
    private boolean fadeIn = true;

    // 유령 애니메이션을 위한 필드들
    private Point ghostPosition;  // 화면상의 유령의 현재 x, y 좌표를 저장하는 Point 객체

    // 유령의 원형 움직임을 위한 각도 값 (라디안 단위)
    private double ghostAngle = 0;

    // 유령이 원을 그리며 움직일 때의 원의 반지름 (픽셀 단위)
    private static final int ANIMATION_RADIUS = 200;
    
    // 유령의 이동 속도를 제어하는 상수 (값이 클수록 더 빠르게 이동)
    private static final int GHOST_SPEED = 1;

    // 배경 음악을 재생하고 제어하기 위한 Clip 객체
    private Clip backgroundSound;
    
    // 배경 음악의 볼륨을 조절하기 위한 컨트롤러
    private FloatControl volumeControl;
    
    // 현재 배경 음악의 재생 상태를 나타내는 플래그 (true: 재생 중, false: 정지)
    private boolean isSoundPlaying = false;

    private GameGUI gameGUI;

    // MainMenu 클래스의 생성자: 게임의 메인 화면을 초기화하고 구성요소들을 설정
    // 생성자라고 생각 하면 됨
    public MainMenu() {
        initializeUI();          // UI 컴포넌트 초기화 및 화면 구성
        initializeAnimationPositions(); // 유령 애니메이션의 시작 위치 설정
        startGhostAnimation();   // 유령의 페이드 인/아웃 애니메이션 시작
        initializeSound();       // 배경 음악 로드 및 재생 설정
    }

    private void initializeUI() {
        setTitle("유령 피하기 보드게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        // ESC 키로 종료
        // -- KeyEvent.VK_ESCAPE: ESC 키를 나타내는 가상 키 코드
        // -- 모디파이 키가 없음을 의미 (예: Ctrl, Alt, Shift 등을 누르지 않음)
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        };

        // WHEN_IN_FOCUSED_WINDOW: 창이 활성화되어 있을 때 키 입력을 감지
        // 키 입력과 동작을 "ESCAPE"라는 이름으로 매핑
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);

        // 화면 크기 가져오기
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // 메인 패널 설정 - 유령 애니메이션이 있는 커스텀 패널
        JPanel mainPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // 어두운 그라데이션 전체적인 메인배경
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(20, 20, 30),
                    0, getHeight(), new Color(40, 40, 60)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // 유령 이미지 효과
                drawGhostEffect(g2d);

                // 안개 효과
                drawFogEffect(g2d);
            }
        };
        mainPanel.setPreferredSize(screenSize);
        add(mainPanel);

        // 게임 제목 뚜렷하게
        JLabel titleLabel = new JLabel("유령을 피해라", SwingConstants.CENTER);
        titleLabel.setFont(createHorrorFont(72));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 100, screenSize.width, 100);

        // 제목에 더 강한 테두리 효과 추가
        titleLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 0, 0), 4),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
            )
        ));

        // 그림자 효과 강화
        JLabel shadowLabel = new JLabel("유령을 피해라", SwingConstants.CENTER);
        shadowLabel.setFont(createHorrorFont(72));
        shadowLabel.setForeground(new Color(255, 0, 0, 180));
        shadowLabel.setBounds(3, 103, screenSize.width, 100);

        mainPanel.add(shadowLabel);
        mainPanel.add(titleLabel);

        // 초기 화면 버튼 생성 및 스타일링
        JButton startButton = createHorrorButton("게임 시작");
        JButton settingsButton = createHorrorButton("설정");
        JButton creditsButton = createHorrorButton("제작자들");

        // 버튼 위치 설정
        int startY = screenSize.height / 2 - 100;

        startButton.setBounds(screenSize.width/2 - BUTTON_WIDTH/2, startY, BUTTON_WIDTH, BUTTON_HEIGHT);
        settingsButton.setBounds(screenSize.width/2 - BUTTON_WIDTH/2, startY + 80, BUTTON_WIDTH, BUTTON_HEIGHT);
        creditsButton.setBounds(screenSize.width/2 - BUTTON_WIDTH/2, startY + 160, BUTTON_WIDTH, BUTTON_HEIGHT);

        // 버튼 이벤트 처리
        startButton.addActionListener(e -> {
            playSpookySound();
            showMapSelectionDialog();
        });

        settingsButton.addActionListener(e -> {
            playSpookySound();
            showCustomDialog("설정",
                "게임 설정\n\n" +
                "- 난이도: 어려움\n" +
                "- 공포 효과: 켜짐\n" +
                "- 유령 속도: 빠름");
        });

        creditsButton.addActionListener(e -> {
            playSpookySound();
            showCreditsAnimation();
        });

        mainPanel.add(startButton);
        mainPanel.add(settingsButton);
        mainPanel.add(creditsButton);

        // 버전 정보 레이블 수정 - 더 선명한 텍스트
        JLabel versionLabel = new JLabel("ver. 1.0.0", SwingConstants.RIGHT);
        versionLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));  // 글자 크기 증가
        versionLabel.setForeground(new Color(255, 50, 50)); //선명한 빨간색 효과
        versionLabel.setBounds(screenSize.width - 200, screenSize.height - 40, 180, 20);
        mainPanel.add(versionLabel);
    }

    // 유령 애니메이션의 초기 위치를 설정하는 메서드
    private void initializeAnimationPositions() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = screenSize.width / 2;
        int centerY = screenSize.height / 3;
        ghostPosition = new Point(centerX, centerY);
    }

    // 유령 애니메이션 업데이트
    private void updateAnimationPositions() {
        // 유령 움직임
        ghostAngle += 0.03 * GHOST_SPEED;
        ghostPosition.x = getWidth()/2 + (int)(ANIMATION_RADIUS * Math.cos(ghostAngle));
        ghostPosition.y = getHeight()/3 + (int)(ANIMATION_RADIUS * Math.sin(ghostAngle));
    }

    private void drawGhostEffect(Graphics2D g2d) {
        updateAnimationPositions();
        
        // 유령 그리기
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ghostAlpha / 255f));
        g2d.setColor(new Color(255, 255, 255, 30));
        
        // 유령 몸체
        g2d.fillOval(ghostPosition.x - 25, ghostPosition.y - 25, 50, 50);
        // 유령 하단부 물결 효과
        g2d.fillOval(ghostPosition.x - 35, ghostPosition.y + 10, 25, 25);
        g2d.fillOval(ghostPosition.x - 15, ghostPosition.y + 15, 25, 25);
        g2d.fillOval(ghostPosition.x + 5, ghostPosition.y + 10, 25, 25);
        
        // 유령 눈
        g2d.setColor(new Color(255, 0, 0, 150));
        g2d.fillOval(ghostPosition.x - 15, ghostPosition.y - 15, 10, 10);
        g2d.fillOval(ghostPosition.x + 5, ghostPosition.y - 15, 10, 10);
    }

    private void drawFogEffect(Graphics2D g2d) {
        Dimension size = getSize();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));

        for (int i = 0; i < 10; i++) {
            int x = (int)(Math.random() * size.width);
            int y = (int)(Math.random() * size.height);
            int fogSize = (int)(Math.random() * 200 + 100);

            g2d.setColor(new Color(255, 255, 255, 20));
            g2d.fillOval(x, y, fogSize, fogSize);
        }
    }
        
    private void startGhostAnimation() {
        ghostAnimationTimer = new Timer(10, e -> {
            if (fadeIn) {
                ghostAlpha += 5;
                if (ghostAlpha >= 255) {
                    fadeIn = false;
                }
            } else {
                ghostAlpha -= 5;
                if (ghostAlpha <= 100) {  // 최소 알파값을 100으로 설정하여 완전히 사라지지 않게 함
                    fadeIn = true;
                }
            }
            repaint();
        });
        ghostAnimationTimer.start();
    }

    private void playSpookySound() {
        //효과음 구현하기
    }

    // 사운드 제어 메소드 추가
    private void toggleSound() {
        if (backgroundSound != null) {
            if (isSoundPlaying) {
                backgroundSound.stop();
            } else {
                backgroundSound.setFramePosition(0);
                backgroundSound.loop(Clip.LOOP_CONTINUOUSLY);  // 다시 시작할 때도 무한 반복 설정
            }
            isSoundPlaying = !isSoundPlaying;
        }
    }

    private void adjustVolume(float change) {
        if (volumeControl != null) {
            try {
                float currentVolume = volumeControl.getValue();
                float newVolume = currentVolume + change;
                
                // 볼륨 범위 제한
                newVolume = Math.max(volumeControl.getMinimum(), Math.min(volumeControl.getMaximum(), newVolume));
                volumeControl.setValue(newVolume);
            } catch (Exception e) {
                System.out.println("볼륨 조절 중 오류 발생: " + e.getMessage());
            }
        }
    }

    // 설정 다이얼로그에 사운드 컨트롤 추가
    private void showCustomDialog(String title, String message) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(20, 20, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 기존 텍스트 영역
        JTextArea textArea = new JTextArea(message);
        textArea.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        textArea.setForeground(Color.WHITE);
        textArea.setBackground(new Color(20, 20, 30));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        panel.add(textArea);

        // 사운드 컨트롤 패널 추가
        if (title.equals("설정")) {
            JPanel soundPanel = new JPanel();
            soundPanel.setLayout(new FlowLayout());
            soundPanel.setBackground(new Color(20, 20, 30));

            // 음악 켜기/끄기 버튼
            JButton toggleButton = new JButton(isSoundPlaying ? "음악 끄기" : "음악 켜기");
            toggleButton.addActionListener(e -> {
                toggleSound();
                toggleButton.setText(isSoundPlaying ? "음악 끄기" : "음악 켜기");
            });

            // 볼륨 조절 슬라이더
            JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, -40, 6, -20);
            volumeSlider.setBackground(new Color(20, 20, 30));
            volumeSlider.setForeground(Color.WHITE);
            volumeSlider.addChangeListener(e -> {
                if (volumeControl != null) {
                    volumeControl.setValue(volumeSlider.getValue());
                }
            });

            soundPanel.add(toggleButton);
            soundPanel.add(volumeSlider);
            panel.add(Box.createVerticalStrut(20));
            panel.add(soundPanel);
        }

        dialog.add(panel, BorderLayout.CENTER);
        
        JButton okButton = createHorrorButton("확인");
        okButton.addActionListener(e -> dialog.dispose());
        dialog.add(okButton, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private Font createHorrorFont(int size) {
        return new Font("맑은 고딕", Font.BOLD, size);
    }

    private JButton createHorrorButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // 버튼 배경
                g2d.setColor(new Color(40, 0, 0));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // 메인 텍스트 위치 계산
                g2d.setFont(createHorrorFont(24));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

                // 텍스트 그림자 효과
                g2d.setColor(new Color(255, 0, 0, 180));
                g2d.drawString(text, textX + 2, textY + 2);
                
                // 글로우 효과
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2d.setColor(new Color(255, 200, 200));
                for(int i = 1; i <= 3; i++) {
                    g2d.drawString(text, textX, textY);
                }
                
                // 최종 텍스트
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setColor(new Color(255, 255, 255));
                g2d.drawString(text, textX, textY);
            }
        };

        button.setFont(createHorrorFont(24));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(40, 0, 0));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));

        // 마우스 오버 효과
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // 버튼 크기 확대 효과
                button.setSize(new Dimension(BUTTON_WIDTH + 20, BUTTON_HEIGHT + 10));
                button.setLocation(button.getX() - 10, button.getY() - 5);
                
                // 배경색 더 강한 빨간색으로
                button.setBackground(new Color(120, 0, 0));
                
                // 테두리 효과 제거
                button.setBorder(null);
                
                // 글자 색상 변경 및 크기 확대
                button.setFont(createHorrorFont(28));
                button.setForeground(new Color(255, 200, 200));
                
                // 효과음 재생
                playSpookySound();
                
                // 버튼 재그리기
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // 원래 크기로 복원
                button.setSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
                button.setLocation(button.getX() + 10, button.getY() + 5);
                
                // 원래 스타일로 복원
                button.setBackground(new Color(40, 0, 0));
                button.setBorder(null);
                button.setFont(createHorrorFont(24));
                button.setForeground(Color.WHITE);
                
                // 버튼 재그리기
                button.repaint();
            }
        });

        return button;
    }

    // 맵 선택 다이얼로그 추가
    private void showMapSelectionDialog() {
        JDialog dialog = new JDialog(this, "맵 선택", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(20, 20, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 맵 설명 레이블
        JLabel descLabel = new JLabel("플레이할 맵을 선택하세요", SwingConstants.CENTER);
        descLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        descLabel.setForeground(Color.WHITE);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(descLabel);
        panel.add(Box.createVerticalStrut(20));

        // 맵 선택 버튼들
        String[][] maps = {
            {"기본 맵", "기본적인 보드 게임 맵입니다."},
            {"원형 트랙", "미완성ㅠㅠ"},
            {"블록 퍼즐", "미완성ㅠㅠ"}
        };

        for (String[] map : maps) {
            JPanel mapPanel = new JPanel(new BorderLayout());
            mapPanel.setBackground(new Color(40, 0, 0));
            mapPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            mapPanel.setMaximumSize(new Dimension(400, 80));
            
            JButton mapButton = new JButton(map[0]) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    // 버튼 배경
                    g2d.setColor(getBackground());
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    // 텍스트 그리기
                    g2d.setFont(createHorrorFont(20));
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                    int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

                    // 텍스트 그림자
                    g2d.setColor(new Color(255, 0, 0, 180));
                    g2d.drawString(getText(), textX + 2, textY + 2);

                    // 메인 텍스트
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(getText(), textX, textY);
                }
            };
            
            mapButton.setPreferredSize(new Dimension(380, 50));
            mapButton.setBackground(new Color(40, 0, 0));
            mapButton.setForeground(Color.WHITE);
            mapButton.setBorderPainted(false);
            mapButton.setFocusPainted(false);
            mapButton.setContentAreaFilled(false);
            
            // 마우스 오버 효과
            mapButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    mapButton.setBackground(new Color(80, 0, 0));
                    playSpookySound();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    mapButton.setBackground(new Color(40, 0, 0));
                }
            });

            // 맵 선택 이벤트
            final int mapIndex = Arrays.asList(maps).indexOf(map);
            mapButton.addActionListener(event -> {
                dialog.dispose();
                dispose();
                SwingUtilities.invokeLater(() -> {
                    try {
                        GameGUI game = new GameGUI();
                        GameMaster gameMaster = new GameMaster();
                        
                        // 선택된 맵 스타일 설정
                        switch(mapIndex) {
                            case 0: // 기본 맵
                                gameMaster.getMapManager().initializeDefaultBoard();
                                gameMaster.setCurrentMapStyle("기본");
                                break;
                            case 1: // 원형 트랙
                                gameMaster.getMapManager().loadMapFromFile("circular_track.txt");
                                gameMaster.setCurrentMapStyle("원형 트랙");
                                break;
                            case 2: // 블록 퍼즐
                                gameMaster.getMapManager().loadMapFromFile("block_puzzle.txt");
                                gameMaster.setCurrentMapStyle("블록 퍼즐");
                                break;
                        }
                        
                        // GUI 모드로 설정하고 게임 시작
                        gameMaster.setBoard(gameMaster.getMapManager().getBoard());
                        game.setGameMaster(gameMaster);
                        gameMaster.setGameGUI(game);  // GameGUI 참조 설정
                        game.setVisible(true);
                        
                        // 게임 시작 효과음 재생
                        playSpookySound();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, 
                            "게임 시작 중 오류가 발생했습니다: " + e.getMessage(),
                            "오류",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
            });

            JLabel descriptionLabel = new JLabel(map[1]);
            descriptionLabel.setForeground(new Color(200, 200, 200));
            descriptionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
            
            mapPanel.add(mapButton, BorderLayout.CENTER);
            mapPanel.add(descriptionLabel, BorderLayout.SOUTH);
            
            panel.add(mapPanel);
            panel.add(Box.createVerticalStrut(10));
        }

        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // 제작자 정보 크레딧 애니메이션
    private void showCreditsAnimation() {
        JDialog dialog = new JDialog(this, "제작자들", true);
        dialog.setUndecorated(true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 배경 그라데이션
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(20, 20, 30),
                    0, getHeight(), new Color(40, 40, 60)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBackground(new Color(20, 20, 30));
        dialog.add(panel);

        // 제작자 이름 크레딧 생성
        String[] credits = {
            "최지태 - 다들 수고 많으셨습니다",
            "정연수 - 덕분에 좋은 경험 했습니다",
            "김유민 - 남은 기간도 화이팅!!",
            "이정호 - 프로젝트도 끝났으니 술 한잔",
            "윤현기 - 하시죠~"
        };
        JLabel[] nameLabels = new JLabel[credits.length];
        
        for (int i = 0; i < credits.length; i++) {
            nameLabels[i] = new JLabel(credits[i], SwingConstants.CENTER);
            nameLabels[i].setFont(new Font("맑은 고딕", Font.BOLD, 20));
            nameLabels[i].setForeground(Color.WHITE);
            nameLabels[i].setBounds(0, dialog.getHeight(), dialog.getWidth(), 40);
            
            // 역할 부분에 색상 변경을 위한 HTML 적용
            String[] parts = credits[i].split(" - ");
            nameLabels[i].setText("<html><font color='white'>" + parts[0] + 
                                "</font> - <font color='#FFB6C1'>" + parts[1] + "</font></html>");
            
            panel.add(nameLabels[i]);
        }

        // 애니메이션 타이머
        Timer[] timers = new Timer[credits.length];
        for (int i = 0; i < credits.length; i++) {
            final int index = i;
            timers[i] = new Timer(2000 + (i * 1500), new ActionListener() {
                private int yPos = dialog.getHeight();
                private Timer moveTimer;

                @Override
                public void actionPerformed(ActionEvent e) {
                    moveTimer = new Timer(10, e2 -> {
                        yPos -= 2;
                        nameLabels[index].setBounds(0, yPos, dialog.getWidth(), 40);
                        
                        if (yPos <= (index * 50) + 20) {
                            moveTimer.stop();
                        }
                    });
                    moveTimer.start();
                }
            });
            timers[i].setRepeats(false);
            timers[i].start();
        }

        // 닫기 버튼
        Timer closeTimer = new Timer(12000, e -> dialog.dispose());
        closeTimer.setRepeats(false);
        closeTimer.start();

        dialog.setVisible(true);
    }

    // 사운드 초기화 메소드 추가
    private void initializeSound() {
        try {
            // 절대 경로 사용
            String absolutePath = "res/resources/324960__amliebsch__ghost-piano-1.wav";
            File soundFile = new File(absolutePath);
            
            if (!soundFile.exists()) {
                System.err.println("파일을 찾을 수 없습니다: " + soundFile.getAbsolutePath());
                soundFile = new File("res/324960__amliebsch__ghost-piano-1.wav");
                if (!soundFile.exists()) {
                    System.err.println("상대 경로에서도 파일을 찾을 수 없습니다: " + soundFile.getAbsolutePath());
                    return;
                }
            }
            
            // 오디오 스트림 생성
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat format = audioStream.getFormat();
            
            // 필요한 경우 오디오 포맷 변환
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    format.getSampleRate(),
                    16,
                    format.getChannels(),
                    format.getChannels() * 2,
                    format.getSampleRate(),
                    false
                );
                audioStream = AudioSystem.getAudioInputStream(targetFormat, audioStream);
            }
            
            // Clip 생성 및 열기
            backgroundSound = AudioSystem.getClip();
            backgroundSound.open(audioStream);
            
            // 볼륨 컨트롤 설정
            try {
                volumeControl = (FloatControl) backgroundSound.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(-20.0f);
            } catch (IllegalArgumentException e) {
                System.out.println("볼륨 조절이 지원되지 않습니다: " + e.getMessage());
                volumeControl = null;
            }
            
            // 무한 반복 재생 설정 (-1은 무한 반복을 의미)
            backgroundSound.loop(Clip.LOOP_CONTINUOUSLY);
            isSoundPlaying = true;
            
        } catch (Exception e) {
            System.err.println("사운드 로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 창이 닫힐 때 사운드 정리
    @Override
    public void dispose() {
        if (backgroundSound != null) {
            backgroundSound.stop();
            backgroundSound.close();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        });
    }
}

