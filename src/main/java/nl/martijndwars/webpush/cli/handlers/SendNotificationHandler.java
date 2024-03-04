package nl.martijndwars.webpush.cli.handlers;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushAsyncService;
import nl.martijndwars.webpush.Subscription;
import nl.martijndwars.webpush.cli.commands.SendNotificationCommand;

public class SendNotificationHandler implements HandlerInterface {

    private final SendNotificationCommand sendNotificationCommand;

    public SendNotificationHandler(SendNotificationCommand sendNotificationCommand) {
        this.sendNotificationCommand = sendNotificationCommand;
    }

    @Override
    public void run() throws Exception {
        PushAsyncService pushService = new PushAsyncService()
            .setPublicKey(sendNotificationCommand.getPublicKey())
            .setPrivateKey(sendNotificationCommand.getPrivateKey())
            .setSubject("mailto:admin@domain.com");

        Subscription subscription = sendNotificationCommand.getSubscription();

        Notification notification = new Notification(subscription, sendNotificationCommand.getPayload());

        PushAsyncService.PushCallback callback = pushService.send(notification);
        callback.wait();
    }
}
