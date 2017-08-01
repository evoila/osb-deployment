export REPOSITORY_MONGO=$repo_service &&
export REPOSITORY_MAIN=$repo_main &&
apt-get update &&
apt-get install -y wget &&
wget $repo_service/mongodb/mongo-template.sh --no-cache && 
chmod +x mongo-template.sh  &&
./mongo-template.sh -d $database_name -u $database_user -p $database_password -e docker
