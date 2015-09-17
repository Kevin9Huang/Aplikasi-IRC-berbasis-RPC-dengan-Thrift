namespace java MessageServiceThrift

typedef string String
struct Message {
  1: string clientName,
  2: string message
}

service MessageService {
  oneway void sendMessage(1:Message msg),
}