// package com.tony.automationserver.statemachine;

// import com.tony.automationserver.ClientSession;

// public class PrepareToReplyState extends State {

//     public PrepareToReplyState(ClientSession session) {
//         super(session);
//     }
    
//     @Override
//     public State Process() {
//         System.out.println("Discarding incoming messages");
//         session.StopListening();
//         return new ReplyState(session);
//     }

//     @Override
//     public boolean instantExecution() {
//         return true;
//     }

// }