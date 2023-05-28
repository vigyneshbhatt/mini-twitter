package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.server.dao.factory.iDAOFactory;
import edu.byu.cs.tweeter.server.dao.iDAO.iAuthTokenDAO;

public class Service {

    public iDAOFactory daoFactory;

    public Service(iDAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }

    public void follow(String userAlias, String followeeAlias){
        daoFactory.getFollowDAO().follow(userAlias, followeeAlias);
        daoFactory.getUserDAO().updateFollowerCount(followeeAlias, 1);
        daoFactory.getUserDAO().updateFolloweeCount(userAlias,1);
    }

    public String putS3Image(RegisterRequest registerRequest) {
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-west-2").build();
        String bucket = "tweeterbucketvigynesh";
        String fileName = registerRequest.getUserAlias();
        byte[] imageBytes = Base64.getDecoder().decode(registerRequest.getImage());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        ObjectMetadata data = new ObjectMetadata();
        data.setContentLength(imageBytes.length);
        data.setContentType("image/jpeg");

        System.out.println("## UserService putS3Image - "+ registerRequest.getUserAlias());
        //file can be read and written to by anyone who has the link to it
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, byteArrayInputStream, data).withCannedAcl(CannedAccessControlList.PublicRead);
        try {
            s3.putObject(putObjectRequest);
        } catch (Exception e) {
            System.out.println("## UserService putS3Image Error: "+ e.getMessage());
        }

        String imageUrl = s3.getUrl(bucket, fileName).toString();

        return imageUrl;
    }

    public static String getSecurePassword(String password) {
//        String salt = getSalt();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
//            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("[Internal Error] Failed to hash password: "+ e.getMessage());
        }
    }

//    public static String getSalt() {
//        try {
//            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
//            byte[] salt = new byte[16];
//            sr.nextBytes(salt);
//            return Base64.getEncoder().encodeToString(salt);
//        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
//            throw new RuntimeException("[Internal Error] Failed get Salt: "+ e.getMessage());
//        }
//    }

}
