package com.picpay.picpaysimplificado.services;

import com.picpay.picpaysimplificado.domain.transaction.Transaction;
import com.picpay.picpaysimplificado.domain.user.User;
import com.picpay.picpaysimplificado.dtos.TransactionDTO;
import com.picpay.picpaysimplificado.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private RestTemplate restTemplate;

    public Transaction createTransaction(TransactionDTO dto) throws Exception {
        User sender = userService.findUserById(dto.senderId());
        User receiver = userService.findUserById(dto.receiverId());

        userService.validateTransaction(sender, dto.value());

        boolean  isAuthorized = authorizeTransaction(sender, dto.value());
        if(!isAuthorized) {
            throw new Exception("Transação não autorizada");
        }

        Transaction transaction = new Transaction();
        transaction.setAmount(dto.value());
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setTimestamp(LocalDateTime.now());

        sender.setBalance(sender.getBalance().subtract(dto.value()));
        receiver.setBalance(receiver.getBalance().add(dto.value()));

        repository.save(transaction);
        userService.saveUser(sender);
        userService.saveUser(receiver);

        notificationService.sendNotification(sender, "Transação realizada com sucesso");

        return transaction;
    }

    public boolean authorizeTransaction(User sender, BigDecimal amount) {
       ResponseEntity<Map> authorizationResponse =  restTemplate.getForEntity("https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc", Map.class);

       if (authorizationResponse.getStatusCode() == HttpStatus.OK) {
           String message = (String) authorizationResponse.getBody().get("message");
           return "Autorizado".equalsIgnoreCase(message);
       } else {
           return false;
       }
    }

}
