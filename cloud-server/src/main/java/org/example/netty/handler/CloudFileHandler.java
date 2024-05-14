package org.example.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.*;
import org.example.netty.CloudServer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class CloudFileHandler extends SimpleChannelInboundHandler<CloudMessage> {
    private Path currentDir;
    private final Path homeDir;
    private String login;

    public CloudFileHandler() {
        currentDir = Path.of("serverFiles");
        homeDir = currentDir;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        log.debug(currentDir.toString() + " is sending...");
//        ctx.writeAndFlush(new ListFiles(currentDir));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        if (cloudMessage instanceof FileRequest fileRequest) {
            ctx.writeAndFlush(new FileMessage(currentDir.resolve(fileRequest.getName())));

        } else if (cloudMessage instanceof FileMessage fileMessage) {
            Files.write(currentDir.resolve(fileMessage.getName()), fileMessage.getData());
            ctx.writeAndFlush(new ListFiles(currentDir));

        } else if (cloudMessage instanceof ChangePathOnServerRequest changePathOnServerRequest) {
            String fileName = changePathOnServerRequest.getName();
            if (Files.isDirectory(currentDir.resolve(fileName).normalize())) {
                if (fileName.equals("..")) {
                    // todo: homeDir.resolve(login) bad...
                    if (!currentDir.equals(homeDir.resolve(login))) {
                        currentDir = currentDir.resolve(fileName).normalize();
                    }
                    ctx.writeAndFlush(new ListFiles(currentDir));
                    return;
                }
                currentDir = currentDir.resolve(fileName).normalize();
                ctx.writeAndFlush(new ListFiles(currentDir));
            }

        } else if (cloudMessage instanceof DeleteRequest deleteRequest) {
            Files.delete(currentDir.resolve(deleteRequest.getName()));
            ctx.writeAndFlush(new ListFiles(currentDir));

        } else if (cloudMessage instanceof RegistrationRequest registrationRequest) {
            ctx.writeAndFlush(new RegistrationResponse(CloudServer.getAuthController()
                    .regUser(registrationRequest.getLogin(), registrationRequest.getPassword())));

        } else if (cloudMessage instanceof AuthRequest authRequest) {
            boolean isSuccess = CloudServer.getAuthController().authenticate(authRequest.getLogin(), authRequest.getPassword());
            ctx.writeAndFlush(new AuthResponse(isSuccess));
            if (isSuccess){
                this.login = authRequest.getLogin();
                goToUserDir(login);
                ctx.writeAndFlush(new ListFiles(currentDir));
            }
        }
    }

    private void goToUserDir(String login) {
        //todo: warning?
        new File(homeDir.resolve(login).toString()).mkdirs();
        currentDir = homeDir.resolve(login);
    }
}
