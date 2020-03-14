
. ./setenv.sh

pid=$(./check.sh)
if [ "$pid" == "0" ]
then
    echo "No instance of $NAME is running"
else
    echo "Killing instance of $NAME $pid"
    kill -9 "$pid"
fi