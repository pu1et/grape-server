package com.nexsol.grape;

import com.nexsol.grape.repository.ImageRepository;
import com.nexsol.grape.repository.S3ImageRepository;
import com.nexsol.grape.service.ImageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    /*
    private final DataSource dataSource;
    private final EntityManager em;


    @Autowired
    public SpringConfig(DataSource dataSource, EntityManager em){
        this.dataSource = dataSource;
        this.em = em;
    }

    */
    @Bean
    public ImageService imageService(){ return new ImageService(imageRepository());}

    @Bean
    public ImageRepository imageRepository(){ return new S3ImageRepository(); }

/*
    @Bean
    public UserService userService(){ return new UserService(userRepository()); }

    @Bean
    public UserRepository userRepository(){ return new JpaUserRepository(em); }


 */
}
