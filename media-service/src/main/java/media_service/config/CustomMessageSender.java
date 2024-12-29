package media_service.config;

//@Component
//@Slf4j
//public class CustomMessageSender {
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    public void sendErrorMessageToProducer(String correlationId, String replyToQueue, String message, int code) throws JsonProcessingException {
//        String res = new ObjectMapper().writeValueAsString
//                (ApiResponse.builder()
//                        .data(null)
//                        .message(message)
//                        .code(code)
//                        .build());
//
//        rabbitTemplate.convertAndSend(
//                "",
//                replyToQueue,
//                res,
//                m -> {
//                    m.getMessageProperties().setCorrelationId(correlationId);
//                    return m;
//                }
//        );
//    }
//
//    public void sendResponseDataToProducer(String correlationId, String replyToQueue, Object data) throws JsonProcessingException {
//        String res = new ObjectMapper().writeValueAsString
//                (ApiResponse.builder()
//                        .data(null)
//                        .message("OK")
//                        .data(data)
//                        .code(200)
//                        .build());
//
//        System.out.println("resCac: "+ res);
//
//        rabbitTemplate.convertAndSend(
//                "",
//                replyToQueue,
//                res,
//                m -> {
//                    m.getMessageProperties().setCorrelationId(correlationId);
//                    return m;
//                }
//        );
//    }
//
//}
