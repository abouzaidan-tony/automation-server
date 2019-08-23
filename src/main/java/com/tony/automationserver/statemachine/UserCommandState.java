// package com.tony.automationserver.statemachine;

// import com.tony.automationserver.Session;
// import com.tony.automationserver.command.MessageAnalyzer;
// import com.tony.automationserver.messages.Message;
// import com.tony.automationserver.messages.MessageBuilder;
// import com.tony.automationserver.messages.Message.MessageType;

// public class UserCommandState extends State {

//     private MessageAnalyzer analyzer;

//     public UserCommandState(Session session, MessageAnalyzer analyzer) {
//         super(session);
//         this.analyzer = analyzer;
//     }

//     @Override
//     public State Process() {
//         Message message;
//         State nextState = null;
//         try{
//             message = new Message(data, MessageType.INITIAL, session.getClient());
//             if(!message.KeepAlive())
//                 nextState = new FinalState(session);
//             else
//                 nextState = new PrepareToReplyState(session);

//             analyzer.Process(message);

//         }catch(IllegalArgumentException ex){
//             message = new MessageBuilder()
//                 .setFromClient(session.getClient())
//                 .setToId("")
//                 .setMessage("Wrong Message Format")
//                 .setMessageType(MessageType.ERROR)
//                 .build();
//             nextState = new FinalState(session);
//         }
        
//         System.out.println("Processing Command");
//         return nextState;    
//     }

//     @Override
//     public boolean instantExecution() {
//         return false;
//     }

// }