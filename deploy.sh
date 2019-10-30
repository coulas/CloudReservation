#!/bin/sh

aws cloudformation deploy --template-file infra-aws.yml --stack-name cloud-reservation

USER_POOL_ID=$(aws cognito-idp list-user-pools --max-results 1 | jq -r '.UserPools[].Id')
USER_POOL_CLIENT_ID=$(aws cognito-idp list-user-pool-clients --user-pool-id $USER_POOL_ID | jq -r '.UserPoolClients[].ClientId')
USER_POOL_CLIENT_SECRET=$(aws cognito-idp describe-user-pool-client --user-pool-id $USER_POOL_ID --client-id $USER_POOL_CLIENT_ID | jq -r '.UserPoolClient.ClientSecret')

# java -jar ./BookingReference/java/target/bookingreference-0.0.1-SNAPSHOT.jar 
# java -jar ./TrainData/java/target/traindata-0.0.1-SNAPSHOT.jar
java -Dspring.security.oauth2.client.registration.cognito.clientId=$USER_POOL_CLIENT_ID -Dspring.security.oauth2.client.registration.cognito.clientSecret=$USER_POOL_CLIENT_SECRET -jar ./TrainReservation/infrastructure/target/trainreservation-0.0.1-SNAPSHOT.jar

