package ru.samsung.itschool.book.cells;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HeaderViewListAdapter;


import java.lang.reflect.Array;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import task.Stub;
import task.Task;

public class CellsActivity extends Activity implements OnClickListener,
        OnLongClickListener {
    // Класс обработки игры

    private int WIDTH = 6;
    private int frame = 0;
    private int HEIGHT = 6;
    private Button[][] cells;
    private boolean isChoosen = false, reset = false;
    private Button targetButton = null;
    private Button targetButton2 = null;

    private String[][] field = new String[HEIGHT][WIDTH];
    private boolean[][] opened = new boolean[HEIGHT][WIDTH];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Действия при создании
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cells);
        makeCells();
        generate();
        Task.showMessage(this, "Игра 'Карточки' Найдите все пары карточек");
    }

    void generate() {
        // Инициализация поля
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                field[i][j] = "";
                opened[i][j] = false;
            }
        }
        int[] indices = new int[HEIGHT * WIDTH];
        for (int i = 0; i < indices.length; i++)
            indices[i] = i;
        for (int i = 0; i < indices.length; i++) {
            for (int j = i + 1; j < indices.length; j++) {
                if (Math.random() > 0.5) {
                    indices[i] ^= indices[j];
                    indices[j] ^= indices[i];
                    indices[i] ^= indices[j];
                }
            }
        }
        String charKit = "abcd<>%@&*$#1234~^";
        for (int i = 0; i < charKit.length(); i++) {
            int index1 = indices[i * 2];
            field[index1 / WIDTH][index1 % WIDTH] = charKit.substring(i, i + 1);
            int index2 = indices[i * 2 + 1];
            field[index2 / WIDTH][index2 % WIDTH] = charKit.substring(i, i + 1);
        }
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                cells[i][j].setText(field[i][j]);
            }
        }
        showCells();
    }

    void showCells() {
        // Отображение поля
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (opened[i][j]) {
                    cells[i][j].setText(field[i][j]);
                } else {
                    cells[i][j].setText("");
                    cells[i][j].setBackgroundColor(Color.WHITE);
                }
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    @Override
    public void onClick(View v) {
        // Обработка нажатия на кнопку

        Button tappedCell = (Button) v;
        int tappedX = getX(tappedCell);
        int tappedY = getY(tappedCell);
        if (!opened[tappedY][tappedX]) {
            if (targetButton == null) {
                targetButton = tappedCell;
                opened[tappedY][tappedX] = true;
                targetButton.setBackgroundColor(getColor(R.color.pear));
            } else if (targetButton2 == null) {
                opened[tappedY][tappedX] = true;
                targetButton2 = tappedCell;
                targetButton2.setBackgroundColor(getColor(R.color.pear));
                int x = getX(targetButton), y = getY(targetButton);
                if (field[y][x].equals(field[tappedY][tappedX])) {
                    targetButton.setBackgroundColor(Color.GREEN);
                    targetButton2.setBackgroundColor(Color.GREEN);
                    targetButton = null;
                    targetButton2 = null;
                    isChoosen = false;
                    if (CheckWin()) {
                        Task.showMessage(this, "Поздравляем, вы выиграли!");
                        generate();
                        showCells();
                    }
                }
                showCells();
            }
            else {
                int x1 = getX(targetButton), y1 = getY(targetButton);
                int x2 = getX(targetButton2), y2 = getY(targetButton2);
                opened[y1][x1] = false;
                opened[y2][x2] = false;
                targetButton = null;
                targetButton2 = null;
                targetButton = tappedCell;
                opened[tappedY][tappedX] = true;
                targetButton.setBackgroundColor(getColor(R.color.pear));
            }
        }
        showCells();
    }

    boolean CheckWin() {
        // Проверка победы
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (!opened[i][j])
                    return false;
            }
        }
        return true;
    }

    int getX(View v) {
        return Integer.parseInt(((String) v.getTag()).split(",")[1]);
    }

    int getY(View v) {
        return Integer.parseInt(((String) v.getTag()).split(",")[0]);
    }

    void makeCells() {
        // Создание элементов интерфейса
        cells = new Button[HEIGHT][WIDTH];
        GridLayout cellsLayout = (GridLayout) findViewById(R.id.CellsLayout);
        cellsLayout.removeAllViews();
        cellsLayout.setColumnCount(WIDTH);
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                cells[i][j] = (Button) inflater.inflate(R.layout.cell, cellsLayout, false);
                cells[i][j].setOnClickListener(this);
                cells[i][j].setOnLongClickListener(this);
                cells[i][j].setTag(i + "," + j);
                cells[i][j].setText(field[i][j]);
                cellsLayout.addView(cells[i][j]);
            }
        }
    }
}