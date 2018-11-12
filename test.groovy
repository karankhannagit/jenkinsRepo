import jenkins.model.*
import java.util.Arrays
import com.cloudbees.jenkins.plugins.amazonecs.*
import com.cloudbees.jenkins.plugins.amazonecs.ECSCloud
import com.cloudbees.jenkins.plugins.amazonecs.ECSTaskTemplate
import com.cloudbees.jenkins.plugins.amazonecs.ECSTaskTemplate.MountPointEntry
import com.cloudbees.jenkins.plugins.amazonecs.ECSTaskTemplate.EnvironmentEntry
import com.amazonaws.services.ecs.model.LaunchType
import com.amazonaws.services.ecs.model.NetworkMode
import com.amazonaws.services.ecs.model.*
import com.amazonaws.services.ecs.model.Volume
import javax.annotation.Nullable

cloud_name = System.getenv('CLOUD_NAME') ?: "ECS-SLAVES"
ecs_cluster_arn = System.getenv('ECS_CLUSTER_ARN') ?: "arn:aws:ecs:us-west-1:1234567890:cluster/jenkins"
aws_region = System.getenv('AWS_REGION') ?: 'us-west-1'
jenkins_url = System.getenv('JENKINS_URL') ?: 'http://'+"curl -s http://169.254.169.254/latest/meta-data/local-ipv4".execute().text+':8080/'
slave_label = System.getenv('SLAVE_LABEL') ?: 'jnlp-slave'
slave_image = System.getenv('SLAVE_IMAGE') ?: '1234567890.dkr.ecr.us-west-1.amazonaws.com/jenkins-ms:slave'
slave_jenkins_root = System.getenv('SLAVE_JENKINS_ROOT') ?: '/home/jenkins'
slave_cpu = System.getenv('SLAVE_CPU') ?: 1024
slave_memory = System.getenv('SLAVE_MEMORY') ?: 2800
def ecsTask




instance = Jenkins.getInstance()

def mounts = Arrays.asList(
  new MountPointEntry(
    name="docker",
    sourcePath="/var/run/docker.sock",
    containerPath="/var/run/docker.sock",
    readOnly=false)
)

try {
 ecsTask = new ECSTaskTemplate(
      templateName="",
      label='jnlp-slave',
      taskDefinitionOverride='mytastdefoverride',
      image='someImage',
      launchType=LaunchType.fromValue('EC2'),
      networkMode='bridge',
      remoteFSRoot=slave_jenkins_root,
      memory=2800,
      memoryReservation=0,
      cpu=1024,
      subnets=null,
      securityGroups=null,
      assignPublicIp=false,
      privileged=false,
      containerUser=null,
      logDriverOptions=null,
      environments=null,
      extraHosts=null,
      mountPoints=null,
      portMappings=null
      )

    } catch (Exception ex){
        println(ex)
    }


ecsCloud = new ECSCloud(
  name=cloud_name,
  templates=Arrays.asList(ecsTask),
  credentialsId=null,
  cluster=ecs_cluster_arn,
  regionName=aws_region,
  jenkinsUrl=jenkins_url,
  slaveTimoutInSeconds=60
)

def clouds = instance.clouds
clouds.add(ecsCloud)
instance.save()
