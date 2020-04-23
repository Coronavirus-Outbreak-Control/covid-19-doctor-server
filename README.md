# Deployment instructions

## Step 1 - Java settings
In order to allow java to compile, it need a `settings.xml` file.

This file contains the secrets needed to the application to properly work.

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      https://maven.apache.org/xsd/settings-1.0.0.xsd">
    <profiles>
        <profile>
            <id>production</id>
            <properties>
                <env>production</env>
                <jwt_secret>XXXXXXXXXXXX</jwt_secret>
                <redis_dsn>redis://XXXXXXXXXXXX:6379</redis_dsn>
                <mysql_dsn>jdbc:mysql://XXXXXXXXXXXX:3306/api_coronaviruscheck?useLegacyDatetimeCode=false&amp;serverTimezone=Europe/Rome&amp;characterEncoding=utf-8&amp;rewriteBatchedStatements=true</mysql_dsn>
                <mysql_username>XXXXXXXXXXXX</mysql_username>
                <mysql_password>XXXXXXXXXXXX</mysql_password>
                <twilio_account_sid>XXXXXXXXXXXX</twilio_account_sid>
                <twilio_auth_token>XXXXXXXXXXXX</twilio_auth_token>
                <AWS_ACCESS_KEY_ID>XXXXXXXXXXXX</AWS_ACCESS_KEY_ID>
                <AWS_SECRET_ACCESS_KEY>XXXXXXXXXXXX</AWS_SECRET_ACCESS_KEY>
            </properties>
        </profile>
        <profile>
            <id>development</id>
            <properties>
                <env></env>
                <jwt_secret>my_secret_key</jwt_secret>
                <redis_dsn>redis://localhost:6379</redis_dsn>
                <mysql_dsn>jdbc:mysql://localhost:3306/api_coronaviruscheck?useLegacyDatetimeCode=false&amp;serverTimezone=Europe/Rome&amp;characterEncoding=utf-8&amp;rewriteBatchedStatements=true</mysql_dsn>
                <mysql_username>root</mysql_username>
                <mysql_password>example</mysql_password>
                <twilio_account_sid></twilio_account_sid>
                <twilio_auth_token></twilio_auth_token>
                <AWS_ACCESS_KEY_ID></AWS_ACCESS_KEY_ID>
                <AWS_SECRET_ACCESS_KEY></AWS_SECRET_ACCESS_KEY>
            </properties>
        </profile>
    </profiles>
</settings>
```

Replace the `XXXXXXXXXXXX` with your values.

## Step 2 - Docker
In the root directory of this project create a file named `.env` file to allow docker-compose to get environment parameters.
The `.env` file contains only this parameter
```
_env=production
``` 

Modify the `docker-compose.yml` file to use your own preferred registry where to push the images:
Ex:
```diff
-     image: registry.gitlab.com/coronavirus-outbreak-control/covid-19-doctor-server
+     image: my_registry.io/coronavirus-outbreak-control/covid-19-doctor-server
```

Run docker compose to build and push the image:
```shell script
# docker-compose build
# docker-compose push --ignore-push-failure
```

## Step 3 - Deploy
We used a simple ansible playbook to deploy on AWS cloud EC2 instance.

Go to the `docker/ansible_deploy` directory, create a file named `hosts` and write the ip of your EC2 instances, of course a public ip is needed or a VPN for private AWS ip.

```shell script
# cat hosts
[webserver]
18.xx.xx.xx
19.xx.xx.xx

[webserver:vars]
ansible_python_interpreter=/usr/bin/python3
```

Create a file with your registry credentials and substitute it's name in the `covid19-doctor-playbook.yml` file.

```shell script
# cat my_just_created_login_file.yml
email: my-mail@gmail.com
password: my-password
```

```diff
  tasks:
-   - include_vars: gitlab-registry.login.yml
+   - include_vars: my_just_created_login_file.yml
```

Add amazon pem file to your ssh agent:
```shell script
# ssh-add ~/.ssh/my-amazon.pem
```

Run ansible and wait for deploy:
```shell script
# ansible-playbook -i hosts -u ubuntu covid19-doctor-playbook.yml
```