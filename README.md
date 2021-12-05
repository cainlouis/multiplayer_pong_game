# Multiplayer Pong Game

## Team Members:

- Nael Louis (1934115)
- Rodrigo Rivas Alfaro (1910674)
- Daniel Lam (1932789)

## Description

This project consists of a Multiplayer Pong Game originally created by Almas Baimagambetov ([GitHub](https://github.com/AlmasB)). Pong is a 2D sports game in which two players try to score against each other while controlling paddles up and down until one player reaches the maxiumum of 10 points. In our version, the game has been modified and altered so that it implements a Multiplayer functionality for two players to connect with each other through IP addresses and play simultaneously (one of which is host, the other is client). Players also have the option to save and load the state of their game and their files will be safely encrypted and decrypted using a SHA-256 algorithm.

The project is based on Java and a Java Game Library called FXGL which provides the functionality for the server and client. GUI is implemented using the JavaFX library.

## How To Build

The project contains many necessary dependancies that are crucial for the project. Project can be run on Windows and Linux machines.

1. Set up Java on your machine by installing the latest Java JDK and SDK. 
2. Clone the project and open up NetBeans.
3. Verify that all the dependancies have been installed (FXGL Library).
4. Build the project and Run it.

If everything goes well, you should be lead to the Pong Main Menu screen!

## How To Play

**OBJECTIVE: Score 10 points before your opponent by sending the ball to the other side!**

_1 player only:_

1. Launch the application twice to have two Pong Game window and select "New Game".
2. Select one window as the host first and then select the other as client.
3. On the client, connect to your current IP address (use ipconfig on cmd) or connect using 127.0.0.1 (localhost).
4. Once you've automatically connected to the game, play and win!

_2 players:_

1. Launch the game and select new game. 
2. Have one user selected as host and the other as client.
3. On client, type in the host's IP address to connect to the server (localhost won't work here!)
4. Play and win against your opponent!

© Created by Nael Louis (1934115), Rodrigo Rivas Alfaro (1910674) & Daniel Lam (1932789)
No rights reserved.
