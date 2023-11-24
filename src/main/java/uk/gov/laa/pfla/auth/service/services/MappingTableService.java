package uk.gov.laa.pfla.auth.service.services;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import uk.gov.laa.pfla.auth.service.dao.MappingTableDao;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;
import uk.gov.laa.pfla.auth.service.responses.ReportListResponse;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import uk.gov.laa.pfla.auth.service.models.MappingTableModel;

import java.sql.ResultSet;
import java.sql.SQLException;

@Service
@Slf4j
public class MappingTableService {

    private final ModelMapper mapper = new ModelMapper();


    private final MappingTableDao mappingTableDao;

    private final List<ReportListResponse> reportListResponses = new ArrayList<>();

    public MappingTableService(MappingTableDao mappingTableDao) {
        this.mappingTableDao = mappingTableDao;
    }

    public List<ReportListResponse> createReportListResponseList() throws Exception {
        mappingTableDao.setupDB();
        reportListResponses.clear(); // Prevent response data accumulating after multiple requests

        //Fetching reportList items from database
        List<MappingTableModel> mappingTableObjectList = mappingTableDao.fetchReportList();

        mappingTableObjectList.forEach(obj -> {
            ReportListResponse reportListResponse = mapper.map(obj, ReportListResponse.class);

            reportListResponses.add(reportListResponse);

        });




        return reportListResponses;
    }



}