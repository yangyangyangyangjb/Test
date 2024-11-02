package com.example.service;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class SnakeGame extends JPanel {

    private static final long serialVersionUID = 1L;

    private int gridSize = 20; // 每个格子的大小
    private int width = 400; // 游戏区域宽度
    private int height = 400; // 游戏区域高度
    private int foodX;
    private int foodY;
    private LinkedList<int[]> snake = new LinkedList<>();
    private int direction = 1; // 1:右, 2:下, 3:左, 4:上
    private boolean gameOver = false;

    public SnakeGame() {
        initSnake();
        generateFood();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_UP && direction!= 2) {
                    direction = 4;
                } else if (keyCode == KeyEvent.VK_DOWN && direction!= 4) {
                    direction = 2;
                } else if (keyCode == KeyEvent.VK_LEFT && direction!= 1) {
                    direction = 3;
                } else if (keyCode == KeyEvent.VK_RIGHT && direction!= 3) {
                    direction = 1;
                }
            }
        });
    }

    private void initSnake() {
        snake.add(new int[] { width / 2 / gridSize, height / 2 / gridSize });
    }

    private void generateFood() {
        foodX = (int) (Math.random() * width / gridSize);
        foodY = (int) (Math.random() * height / gridSize);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 绘制背景
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        // 绘制食物
        g.setColor(Color.RED);
        g.fillRect(foodX * gridSize, foodY * gridSize, gridSize, gridSize);

        // 绘制蛇
        g.setColor(Color.GREEN);
        for (int[] pos : snake) {
            g.fillRect(pos[0] * gridSize, pos[1] * gridSize, gridSize, gridSize);
        }
    }

    private void moveSnake() {
        int[] head = snake.getFirst();
        int newX = head[0];
        int newY = head[1];

        if (direction == 1) {
            newX++;
        } else if (direction == 2) {
            newY++;
        } else if (direction == 3) {
            newX--;
        } else if (direction == 4) {
            newY--;
        }

        if (newX < 0 || newX >= width / gridSize || newY < 0 || newY >= height / gridSize) {
            gameOver = true;
            return;
        }

        if (newX == foodX && newY == foodY) {
            generateFood();
        } else {
            snake.removeLast();
        }

        snake.addFirst(new int[] { newX, newY });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("贪吃蛇游戏");
        SnakeGame game = new SnakeGame();

        frame.add(game);
        frame.setSize(game.width, game.height);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        while (!game.gameOver) {
            game.moveSnake();
            game.repaint();

            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}