package com.womack.spring6restmvcmaven.bootstrap;

import com.womack.spring6restmvcmaven.entities.Beer;
import com.womack.spring6restmvcmaven.entities.Customer;
import com.womack.spring6restmvcmaven.model.BeerCSVRecord;
import com.womack.spring6restmvcmaven.model.BeerStyle;
import com.womack.spring6restmvcmaven.repositories.BeerRepository;
import com.womack.spring6restmvcmaven.repositories.CustomerRepository;
import com.womack.spring6restmvcmaven.services.BeerCSVService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BootStrapData implements CommandLineRunner {

    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;
    private final BeerCSVService beerCsvService;

    @Transactional
    @Override
    public void run(String... args) throws Exception {
        loadBeerData();
        loadCSVData();
        loadCustomerData();
    }

    private void loadCSVData() throws FileNotFoundException {
        if (beerRepository.count() < 10) {
            File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");
//            File file = new File(System.getProperty("user.dir")+ "/src/main/resources/csvdata/beers.csv");
            List<BeerCSVRecord> recs = beerCsvService.convertCSV(file);

            recs.forEach(beerCSVRecord -> {
                BeerStyle beerStyle = switch (beerCSVRecord.getStyle()) {
                    case "American Pale Lager", "Munich Helles Lager" -> BeerStyle.LAGER;
                    case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" ->
                            BeerStyle.ALE;
                    case "American IPA", "American Double / Imperial IPA", "Belgian IPA",
                         "English Brown Ale", "American Amber / Red Ale" -> BeerStyle.IPA;
                    case "American Porter" -> BeerStyle.PORTER;
//                    case "Gose" -> BeerStyle.GOSE;
                    case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
                    case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
                    case "Fruit / Vegetable Beer", "American Pale Wheat Ale", "Hefeweizen",
                         "American Dark Wheat Ale", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
                    case "English Pale Ale" -> BeerStyle.PALE_ALE;
                    default -> BeerStyle.PILSNER;
                };

                beerRepository.save(Beer.builder()
                        .beerStyle(beerStyle)
                        .beerName(StringUtils.abbreviate(beerCSVRecord.getBeer(), 30))
                        .quantityOnHand(Integer.valueOf(beerCSVRecord.getCount_y()))
                        .upc(beerCSVRecord.getRow().toString())
                        .price(BigDecimal.TEN)
//                        .createdDate(LocalDateTime.now())
//                        .updateDate(LocalDateTime.now())
                        .build());

            });
        }
    }

    private void loadCustomerData() {
        if (customerRepository.count() == 0) {
            Customer customer1 = Customer.builder()
                    .firstName("Michael")
                    .lastName("Womack")
                    .createDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            Customer customer2 = Customer.builder()
                    .firstName("Emmie")
                    .lastName("Laroosky")
                    .createDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            Customer customer3 = Customer.builder()
                    .firstName("John")
                    .lastName("Thompson")
                    .createDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3));
//        customerRepository.save(customer1);
//        customerRepository.save(customer2);
        }
    }

    private void loadBeerData() {
        if (beerRepository.count() == 0) {
            Beer beer1 = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("123456")
                    .price(new BigDecimal("12.99"))
                    .quantityOnHand(122)
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            Beer beer2 = Beer.builder()
                    .beerName("Crank")
                    .beerStyle(BeerStyle.PALE_ALE)
                    .upc("12356222")
                    .price(new BigDecimal("11.99"))
                    .quantityOnHand(392)
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            Beer beer3 = Beer.builder()
                    .beerName("Sunshine City")
                    .beerStyle(BeerStyle.IPA)
                    .upc("12356")
                    .price(new BigDecimal("13.99"))
                    .quantityOnHand(144)
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

            beerRepository.saveAll(Arrays.asList(beer1, beer2, beer3));
        }
    }
}
