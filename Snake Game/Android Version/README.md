# Android Snake Game [![Build Status][travis-image]][travis-url]

This app is classic snake game where the user can play the game and store high score.

In this game snake will die i.e. game will be over if snake hits the wall or its head get stuck with its body.
Also as score increases speed of snake will increase.

## Code explanation

I have used canvas to draw the snake and field using drawrect and other method calls.
Also I have created one thread to run the snake code to have user freedom of doing other things in main thread.
Also for storing high score of user I have used SharedPreferences to store it in GAME_DATA file.


<!-- Markdown link & img dfn's -->
[travis-image]: https://img.shields.io/travis/dbader/node-datadog-metrics/master.svg?style=flat-square
[travis-url]: https://travis-ci.org/dbader/node-datadog-metrics
