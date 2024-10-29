package com.womack.spring6restmvcmaven.services;

import com.womack.spring6restmvcmaven.model.BeerCSVRecord;

import java.io.File;
import java.util.List;

public interface BeerCSVService {
    List<BeerCSVRecord> convertCSV(File csvFile);
}
