/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.classicalmusicquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    private static final String GAME_FINISHED = "game_finished";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView highScoreTextView = (TextView) findViewById(R.id.highscoreText);

        // Get the high and max score.
        int highScore = QuizUtils.getHighScore(this);
        int maxScore = Sample.getAllSampleIDs(this).size() - 1;

        // Set the high score text.
        String highScoreText = getString(R.string.high_score, highScore, maxScore);
        highScoreTextView.setText(highScoreText);

        // If the game is over, show the game finished UI.
        if(getIntent().hasExtra(GAME_FINISHED)){
            TextView gameFinishedTextView = (TextView) findViewById(R.id.gameResult);
            TextView yourScoreTextView = (TextView) findViewById(R.id.resultScore);

            Integer yourScore = QuizUtils.getCurrentScore(this);
            String yourScoreText = getString(R.string.score_result, yourScore, maxScore);
            yourScoreTextView.setText(yourScoreText);

            gameFinishedTextView.setVisibility(View.VISIBLE);
            yourScoreTextView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * The OnClick method for the New Game button that starts a new game.
     * @param view The New Game button.
     */
    public void newGame(View view) {
        Intent quizIntent = new Intent(this, QuizActivity.class);
        startActivity(quizIntent);
    }
}
