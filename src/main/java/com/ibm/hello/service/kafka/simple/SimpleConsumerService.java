package com.ibm.hello.service.kafka.simple;

import java.time.Duration;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.hello.config.kafka.simple.SimpleKafkaConstants;

@Service
public class SimpleConsumerService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleConsumerService.class);

	Consumer<Long, String> kafkaConsumer;

	
	@Autowired
	public SimpleConsumerService(Consumer<Long, String> kafkaConsumer) {
		super();
		this.kafkaConsumer = kafkaConsumer;
	}

	
	public void runConsumer() {
				
		int noMessageToFetch = 0;

		while (true) {
			final ConsumerRecords<Long, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000)); 
			
			if (consumerRecords.count() == 0) {
				noMessageToFetch++;
				if (noMessageToFetch > SimpleKafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
					break;
				else
					continue;
			}

			consumerRecords.forEach(record -> {
				LOGGER.info("--------------");
				LOGGER.info("Record Key " + record.key());
				LOGGER.info("Record value " + record.value());
				LOGGER.info("Record partition " + record.partition());
				LOGGER.info("Record offset " + record.offset());
			});
			kafkaConsumer.commitAsync();
		}
		kafkaConsumer.close();
	}

}