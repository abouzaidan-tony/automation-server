// package com.tony.automationserver.statemachine;

// import com.tony.automationserver.ClientSession;

// public class ReplyState extends State {

//     public ReplyState(ClientSession session) {
//         super(session);
//     }
    
//     @Override
//     public State Process() {
//         System.out.println("awaiting message...");
//         return new FinalState(session);
//     }

//     @Override
//     public boolean instantExecution() {
//         return false;
//     }

// }