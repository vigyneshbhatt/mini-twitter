@echo off

@REM call gradlew shadowJar > out.txt
echo > out.txt

aws lambda update-function-code --function-name PostStatusIntermediateTaskHandler --zip-file fileb://server/build/libs/server-all.jar >> out.txt
aws lambda update-function-code --function-name UpdateFeedHandler --zip-file fileb://server/build/libs/server-all.jar >> out.txt

aws lambda update-function-code --function-name LoginHandlerLambda --zip-file fileb://server/build/libs/server-all.jar >> out.txt
aws lambda update-function-code --function-name RegisterHandlerLambda --zip-file fileb://server/build/libs/server-all.jar >> out.txt
aws lambda update-function-code --function-name FollowHandlerLambda --zip-file fileb://server/build/libs/server-all.jar >> out.txt
aws lambda update-function-code --function-name UnfollowHandlerLambda --zip-file fileb://server/build/libs/server-all.jar >> out.txt
aws lambda update-function-code --function-name GetFollowersCountLambda --zip-file fileb://server/build/libs/server-all.jar >> out.txt
aws lambda update-function-code --function-name GetFollowingCountLambda --zip-file fileb://server/build/libs/server-all.jar >> out.txt
aws lambda update-function-code --function-name IsFollowerHandlerLambda --zip-file fileb://server/build/libs/server-all.jar >> out.txt
aws lambda update-function-code --function-name LogoutHandlerLambda --zip-file fileb://server/build/libs/server-all.jar >> out.txt
aws lambda update-function-code --function-name GetFeedHandlerLambda --zip-file fileb://server/build/libs/server-all.jar >> out.txt
aws lambda update-function-code --function-name GetStoryHandlerLambda --zip-file fileb://server/build/libs/server-all.jar >> out.txt
aws lambda update-function-code --function-name GetFollowingHandlerLambda --zip-file fileb://server/build/libs/server-all.jar >> out.txt
aws lambda update-function-code --function-name GetFollowersLambda --zip-file fileb://server/build/libs/server-all.jar >> out.txt
aws lambda update-function-code --function-name GetUserHandlerLambda --zip-file fileb://server/build/libs/server-all.jar >> out.txt
aws lambda update-function-code --function-name PostStatusLambda --zip-file fileb://server/build/libs/server-all.jar >> out.txt

