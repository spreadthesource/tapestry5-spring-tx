# Tapestry 5 Spring Tx Plugin

## How to

This contribution allows you to access to a Hibernate session factory configured via 
Spring through your Tapestry businness layer in the same transaction. To configure you only need 
to specify the name of the Hibernate Session Factory spring bean, and the name of the Spring transaction manager.

Ex :

 	public static void contributeApplicationDefaults(MappedConfiguration<String, String> configuration)
    {
        configuration.add(SpringHibernateConstants.SESSION_FACTORY_BEAN_NAME, "sessionFactory");
        configuration.add(SpringHibernateConstants.TX_MANAGER_BEAN_NAME, "transactionManager");
    }

With this contribution Tapestry will not initialize Hibernate and will simply wrap its session source and transaction handling
using the bean provided by Spring. Also, the spring transaction context will be initialized.

## Utils

This contribution also provide an extension to the default spring LocalSessionFactoryBean that will allow you to specify a list
of packages that contain your domain objects. All the beans annotated with @Entity will be added to the Hibernate configuration.

Ex :

	<bean id="sessionFactory"
		class="com.spreadthesource.tapestry.spring.hibernate.TapestryLocalSessionFactoryBean">
		<property name="dataSource" ref="dataSource" />
		<property name="configLocation" value="classpath:hibernate.cfg.xml" />
		<property name="configurationClass" value="org.hibernate.cfg.AnnotationConfiguration" />
		<property name="packageNames">
			<list>
				<value>com.spreadthesource.tapestry.spring.hibernate.model</value>
			</list>
		</property>
	</bean>

For more information, you can have a look at the test application provided with this contribution.

## Maven dependency

To use this plugin, add the following dependency in your `pom.xml`.

	<dependencies>
		...
		<dependency>
			<groupId>com.spreadthesource</groupId>
			<artifactId>tapestry5-spring-tx</artifactId>
			<version>1.0.0-SNAPSHOT</version>
		</dependency>
		...
	</dependencies>
	
	<repositories>
		...
		<repository>
			<id>devlab722-repo</id>
			<url>http://nexus.devlab722.net/nexus/content/repositories/releases
			</url>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>

		<repository>
			<id>devlab722-snapshot-repo</id>
			<url>http://nexus.devlab722.net/nexus/content/repositories/snapshots
			</url>
			<releases>
				<enabled>false</enabled>
			</releases>
		</repository>
		
		...
	</repositories>

## More Informations & contacts

* Blog: http://spreadthesource.com
* Twitter: http://twitter.com/spreadthesource

## License

This project is distributed under Apache 2 License. See LICENSE.txt for more information.

