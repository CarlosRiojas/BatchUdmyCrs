package com.BatchUdemyExample.BatchDemoUdm.config;


import com.BatchUdemyExample.BatchDemoUdm.listener.HwJobExecutionListener;
import com.BatchUdemyExample.BatchDemoUdm.listener.HwStepExecutionListener;
import com.BatchUdemyExample.BatchDemoUdm.model.Product;
import com.BatchUdemyExample.BatchDemoUdm.processor.InMemItemProcessor;
import com.BatchUdemyExample.BatchDemoUdm.reader.InMemReader;
import com.BatchUdemyExample.BatchDemoUdm.writer.ConsoleItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.batch.api.chunk.ItemReader;

@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    private HwJobExecutionListener hwJobExecutionListener;

    @Autowired
    private HwStepExecutionListener hwStepExecutionListener;

    @Autowired
    private InMemItemProcessor inMemItemProcessor;


    @Bean
    public Step step1() {
        return steps.get("step1")
                .listener(hwStepExecutionListener)
                .tasklet(helloWorldTasklet())
                .build();

    }

    public InMemReader reader() {
        return new InMemReader();
    }

    @Bean
    public FlatFileItemReader flatFileItemReader()

    {
        FlatFileItemReader reader = new FlatFileItemReader();
        //Step 1 let reader know where is the file
        reader.setResource(new FileSystemResource("input/product.csv"));
        //Step 2 create the line mapper
        reader.setLineMapper(
                new DefaultLineMapper<Product>() {
                    {
                        setLineTokenizer(new DelimitedLineTokenizer() {
                            {
                                setNames(new String[]{
                                        "productID",
                                        "productName",
                                        "ProductDesc",
                                        "price",
                                        "unit"});//you need to give a property to each token
                            }
                        });
                        setFieldSetMapper(new BeanWrapperFieldSetMapper<Product>() {
                            {
                                setTargetType(Product.class);
                            }
                        });
                    }
                }
        );
        //step 3 tell reader to skip the header

        reader.setLinesToSkip(1);
        return reader;
    }

    @Bean
    public Step step2() {
        return steps.get("step2").<Integer, Integer>chunk(3)
                .reader(flatFileItemReader())
                .writer(new ConsoleItemWriter())
                .build();

    }

    @Bean
    public Job helloworldJob() {
        return jobs.get("helloworldJob")
                .listener(hwJobExecutionListener)
                .start(step1())
                .next(step2())
                .build();
    }

    private Tasklet helloWorldTasklet() {
        return (new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Hello world");
                return RepeatStatus.FINISHED;
            }
        });
    }
}
