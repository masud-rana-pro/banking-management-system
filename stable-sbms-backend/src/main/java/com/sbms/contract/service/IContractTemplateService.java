package com.sbms.contract.service;

import com.sbms.contract.dto.request.ContractTemplateRequest;
import com.sbms.contract.dto.response.ContractTemplateResponse;

import java.util.List;

public interface IContractTemplateService {
    ContractTemplateResponse create(ContractTemplateRequest request);
    List<ContractTemplateResponse> list();
    ContractTemplateResponse getById(Long id);
    ContractTemplateResponse update(Long id, ContractTemplateRequest request);
    ContractTemplateResponse archive(Long id);
    ContractTemplateResponse restore(Long id);
}
