package com.cgd.tinkConnector;

import com.cgd.tinkConnector.Clients.OAuthGrant;
import com.cgd.tinkConnector.Clients.OAuthToken;
import com.cgd.tinkConnector.Clients.TinkClient;
import com.cgd.tinkConnector.Clients.TinkServices;
import com.cgd.tinkConnector.Model.IO.TransactionsUploadRequest;
import com.cgd.tinkConnector.Model.Tink.TinkAccount;
import com.cgd.tinkConnector.Repositories.BatchFilesRepository;
import com.cgd.tinkConnector.Repositories.DatabasePropertiesRepository;
import com.cgd.tinkConnector.Repositories.PCEClientSubscriptionRepository;
import com.cgd.tinkConnector.Repositories.TestUsersRepository;
import com.cgd.tinkConnector.Repositories.TinkUserAccountsRepository;
import com.cgd.tinkConnector.Repositories.TinkUsersRepository;
import com.cgd.tinkConnector.Repositories.UploadRequestsRepository;
import com.cgd.tinkConnector.Utils.DynamicProperties;
import com.cgd.tinkConnector.entities.PCEUploadRequest;
import com.cgd.tinkConnector.entities.TinkUserAccounts;
import com.cgd.tinkConnector.entities.TinkUsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;


public class BaseController {

    private static Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    protected RestTemplate cgdSvc;
    protected RestTemplate tinkSvc;

    protected String clientId;
    protected String clientSecret;


    @Autowired
    protected UploadRequestsRepository requestsRepository;

    @Autowired
    protected TinkUsersRepository usersRepository;

    @Autowired
    protected TinkUserAccountsRepository accountsRepository;

    @Autowired
    protected TestUsersRepository testUsersRepository;

    @Autowired
    protected PCEClientSubscriptionRepository subscriptionsRepository;

    @Autowired
    protected DatabasePropertiesRepository propertiesRepository;

    @Autowired
    protected BatchFilesRepository batchFilesRepository;


    protected void registerServiceCall(TransactionsUploadRequest request, int serviceId, Object payload) {

        this.registerServiceCall(request, serviceId, 1, 200, payload, null);
    }

    protected void registerServiceCallWithError(TransactionsUploadRequest request, int serviceId, int responseCode, Object payload, Exception error) {

        this.registerServiceCall(request, serviceId, 0, responseCode, payload, error);


    }

    protected TinkUsers getTinkUserByTinkId(TinkClient tinkClient, String accessToken, String tinkId) {

        Optional<TinkUsers> tinkUser = this.usersRepository.findById(tinkId);

        if (!tinkUser.isPresent()) {

            // Get user oauth token
            OAuthGrant userToken = tinkClient.usertoken(accessToken, tinkId, TinkClient.USER_SCOPE);
            OAuthToken userAuth = tinkClient.token("authorization_code", null, userToken.getCode(), null);
            tinkUser = Optional.of(tinkClient.getUser(userAuth.getAccessToken()));
            this.usersRepository.save(tinkUser.get());
        }

        return tinkUser.get();

    }

    protected boolean validatePayloadBeforeUpload(int serviceId, Object payload) {

        if (payload == null) return false;


        // ObjectMapper objectMapper = new ObjectMapper();

        String json = null;
        try {
            json = payload.toString();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(json.getBytes());
            byte[] digest = md.digest();
            String hash = DatatypeConverter.printHexBinary(digest).toUpperCase();

            List<PCEUploadRequest> requests = this.requestsRepository.findByPayloadHashAndServiceId(hash, serviceId);
            return requests.isEmpty();


        } catch (Exception e) {

            LOGGER.error("validatePayloadBeforeUpload", e);
            return false;
        }


    }

    protected void initDynamicProperties() {

        if (TinkConnectorConfiguration.properties == null) {

            TinkConnectorConfiguration.properties = new DynamicProperties(this.propertiesRepository);

            if (!TinkConnectorConfiguration.properties.existsProperty(DynamicProperties.CGD_ACTIVATE_UPLOAD_TO_TINK)) {

                TinkConnectorConfiguration.properties.saveProperty(DynamicProperties.CGD_ACTIVATE_UPLOAD_TO_TINK, "false");
            }

            if (!TinkConnectorConfiguration.properties.existsProperty(DynamicProperties.CGD_USER_TRANSLATION_TYPE)) {

                TinkConnectorConfiguration.properties.saveProperty(DynamicProperties.CGD_USER_TRANSLATION_TYPE, "4");
            }
            if (!TinkConnectorConfiguration.properties.existsProperty(DynamicProperties.CGD_ACTIVATE_BATCH_FILE_PROCESSING)) {

                TinkConnectorConfiguration.properties.saveProperty(DynamicProperties.CGD_ACTIVATE_BATCH_FILE_PROCESSING, "false");
            }
        }

    }

    protected boolean uploadAccountsToTink(TinkClient tinkClient, String accessToken, TransactionsUploadRequest request, TinkUsers user, List<TinkAccount> tinkAccounts) {

        if (!TinkConnectorConfiguration.properties.getPropertyAsBoolean(DynamicProperties.CGD_ACTIVATE_UPLOAD_TO_TINK))
            return true;

        if (tinkAccounts.size() == 0) return true;


        List<TinkAccount> accountsToSave = new ArrayList<>();
        try {

            boolean isValid = validatePayloadBeforeUpload(TinkServices.INGEST_ACOUNTS.getServiceCode(), tinkAccounts);

            tinkClient.ingestAccounts(accessToken, user.getExternalUserId(), tinkAccounts);
            accountsToSave.addAll(tinkAccounts);

            registerServiceCall(request, TinkServices.INGEST_ACOUNTS.getServiceCode(), tinkAccounts);
            return true;
        } catch (HttpClientErrorException e) {

            LOGGER.error("processUpload", e);
            if (e.getStatusCode().value() == 409) {
                // uma das contas ja existe ,going account by account mode

                List<TinkAccount> testAccount = new ArrayList<>();
                for (TinkAccount a : tinkAccounts) {

                    testAccount.clear();
                    testAccount.add(a);
                    try {
                        tinkClient.ingestAccounts(accessToken, user.getExternalUserId(), testAccount);

                    } catch (HttpClientErrorException e1) {

                        if (e.getStatusCode().value() == 409) {

                            accountsToSave.add(a);
                        }

                    }

                }
                return true;
            } else {
                registerServiceCallWithError(request, TinkServices.INGEST_ACOUNTS.getServiceCode(), e.getStatusCode().value(), tinkAccounts, e);

            }
            return false;
        } finally {

            try {

                Date now = Calendar.getInstance().getTime();
                for (TinkAccount ac : accountsToSave) {

                    TinkUserAccounts ua = new TinkUserAccounts(ac.getExternalId(), user.getId());
                    ua.setAccountNumber(ac.getNumber());
                    ua.setAccountDescription(ac.getName());
                    ua.setUploadDate(now);
                    this.accountsRepository.save(ua);

                }
                this.accountsRepository.flush();
            } catch (Exception e) {

                LOGGER.error("processUpload", e);
            }

        }


    }


    protected void registerServiceCall(TransactionsUploadRequest request, int serviceId, int errorCode, int responseCode, Object payload, Exception error) {


        try {

            //   ObjectMapper objectMapper = new ObjectMapper();
            PCEUploadRequest upRequest = new PCEUploadRequest();
            upRequest.setNumClient(request.getNumClient());
            upRequest.setRequestDate(Calendar.getInstance().getTime());
            upRequest.setSubscriptionId(request.getSubscriptionId());
            upRequest.setTinkId(request.getTinkId());
            upRequest.setPayload(payload.toString()); //objectMapper.writeValueAsString(payload));
            upRequest.setServiceId(serviceId);
            upRequest.setResponseCode(responseCode);

            if (error != null) {
                upRequest.setError(error.toString());//objectMapper.writeValueAsString(error));

            }
            if (upRequest.getPayload() != null) {


                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(upRequest.getPayload().getBytes());
                byte[] digest = md.digest();
                upRequest.setPayloadHash(DatatypeConverter
                        .printHexBinary(digest).toUpperCase());

            }

            upRequest.setStatusCode(errorCode);
            this.requestsRepository.save(upRequest);

        } catch (Exception e) {

            LOGGER.error(String.format("registerServiceCall : subscription %s", request.getSubscriptionId()), e);
        }

    }


}
