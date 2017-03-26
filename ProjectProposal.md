---
title: 'Project proposal for Cards against Humanity application'
authors: 
- Axel Ekdahl
- Alex Tao
- Mohannad Ahdab
- Mikael Lönn
- Emy Arts
- Alessandro Flaborea
- Debora Scappin
---

# Project Proposal

**Platform:** Android 21 and up  
**Programming languages:** Java, SQL/Json, XML  
  
This is the project proposal for Cards against Humanity app. It describes what the application is, which programming languages
abd platforms will be used and a few possible features that can enhance the Cards against humanity experience.  

## What is Cards Against Humanity
This card game is an already invented and well known game. The general game idea is that there’s a deck of cards and several different players. One of the players (aka the King) is given a card (a black card) containing a sentence that is incomplete. The other players are given a set of other cards (white cards) containing solitary words that supplement the card with the incomplete sentence. The goal is to create the most funny sentence possible. Furthermore, during the playing session one person is assigned the role as the king. The player that is the king decides which player laid the funniest card of the round. Hence the king decides the winner of that specific round. Next round another player is assigned the role as the king. The game ends when the settings (specified by the host) for the table is met. The player with the highest score when the game ends is the winner.

## Main concept of the application
The objective with this project is to create a simple app for the Android platform in which players can play cards against humanity. Aside from the cards against humanity game itself, the application should also support different features that enhances the game experience such as chat rooms. A more detailed description of the features that could be included in the application is explained below.

Important to note is that the key concept of this application is that people are playing against other players. Hence the application won’t support single player modes. When a person want to play Cards against Humanity, the player chooses a table to “sit at” in the application. A table can contain up to 20 players. A player that has not already picked a table can choose to create a new table where other players can join. When the host of the table (the player that created the table) decides that the table contains enough players the host can start the playing session. 

The application should support different types of card decks. Each card deck is related to a category, where all the cards in the card deck relates to that category. E.g. think of a card deck of the category animal. All the cards in that card deck are related to animals.  

Players should be able to blacklist cards. It could be that the player find the card being offending. All cards that are blacklisted from all the players at the table should be excluded from the playing session. 

## Additional features and functionality in the application
During the playing session (when players have chosen a table and the game has started) there is a group chat. This allows people to either react to events in the game or communicate with other people that aren’t physically at the same location. Initial thoughts are that only quick/ predefined commands should be supported in the chat. However, also open/custom text messages could be possible as well if the customer of the application decides that is necessary. 

We have decided to develop the app using peer-to-peer network. One reason to use peer-to-peer is that we don’t need to consider deployment of servers the application would need to communicate with (e.g. it’s possible to argue that the card decks should be stored in a central location). Servers would most likely cost us money, which we would like to avoid if possible. Hence in order for people to be able to play against each other they need to be connected to the same wifi. 

If we would develop this application with a regular client-server approach we could have stored the different card decks at the server. However, since we using a peer-to-peer approach we end up with the solution that the card decks are going to be stored inside the application instead. This can for instance be accomplished by using Sqlite or Json. 
