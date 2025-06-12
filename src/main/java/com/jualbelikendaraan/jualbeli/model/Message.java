package com.jualbelikendaraan.jualbeli.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Kendaraan vehicle;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Constructors
    public Message() {
        this.timestamp = LocalDateTime.now();
    }

    public Message(User sender, User receiver, Kendaraan vehicle, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.vehicle = vehicle;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Kendaraan getVehicle() {
        return vehicle;
    }

    public void setVehicle(Kendaraan vehicle) {
        this.vehicle = vehicle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
               "id=" + id +
               ", sender=" + sender.getUsername() +
               ", receiver=" + receiver.getUsername() +
               ", vehicleId=" + vehicle.getId() +
               ", content='" + content + '\'' +
               ", timestamp=" + timestamp +
               '}';
    }
} 