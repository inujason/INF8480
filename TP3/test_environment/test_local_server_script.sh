echo "Start"
start_time=$(date +%s%3N)


for i in {1..3}
do

for i in {1..30}
do 
	wget localhost:8000 &
done

wait
cd ..
stop_time=$(date +%s%3N)
diff=$(($stop_time-$start_time))
echo "time elapsed: $diff" >> TESTING_SCRIPT.xls

done


wait

