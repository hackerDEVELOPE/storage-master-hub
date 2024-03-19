package org.example.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.*;
import org.example.netty.CloudServer;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class CloudFileHandler extends SimpleChannelInboundHandler<CloudMessage> {
   private Path currentDir;
   private final Path homeDir;

    public CloudFileHandler() {
        currentDir = Path.of("serverFiles");
        homeDir = currentDir;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug(currentDir.toString()+" is sending...");
        ctx.writeAndFlush(new ListFiles(currentDir));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        if (cloudMessage instanceof FileRequest fileRequest) {
            ctx.writeAndFlush(new FileMessage(currentDir.resolve(fileRequest.getName())));

        } else if (cloudMessage instanceof FileMessage fileMessage){
            Files.write(currentDir.resolve(fileMessage.getName()), fileMessage.getData());
            ctx.writeAndFlush(new ListFiles(currentDir));

        } else if (cloudMessage instanceof ChangePathOnServerRequest changePathOnServerRequest){
            String fileName = changePathOnServerRequest.getName();
            if (Files.isDirectory(currentDir.resolve(fileName).normalize())){
                if (fileName.equals("..")){
                    if (!currentDir.equals(homeDir)){
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

        } else if (cloudMessage instanceof  RegistrationRequest registrationRequest){
            CloudServer.getAuthController()
                    .regUser(registrationRequest.getLogin(), registrationRequest.getPassword());

        } else if (cloudMessage instanceof AuthRequest authRequest) {
                ctx.writeAndFlush(new AuthResponse(CloudServer.getAuthController().authenticate(authRequest.getLogin(), authRequest.getPassword())));
        }
    }
}
