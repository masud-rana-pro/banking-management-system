import { Injectable } from '@angular/core';

import { CustomerResponse } from '../../customer/models/customer.model';
import { AccountOpeningRequestResponse, AccountResponse } from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountHolderImageService {

  buildCustomerImageMap(customers: CustomerResponse[]): Record<number, string> {
    return customers.reduce((acc, item) => {
      acc[item.id] = item.profileImageName || '';
      return acc;
    }, {} as Record<number, string>);
  }

  buildCustomerImageByCode(customers: CustomerResponse[]): Record<string, string> {
    return customers.reduce((acc, item) => {
      if (item.customerCode) {
        acc[item.customerCode.trim().toLowerCase()] = item.profileImageName || '';
      }
      return acc;
    }, {} as Record<string, string>);
  }

  buildCustomerImageByName(customers: CustomerResponse[]): Record<string, string> {
    return customers.reduce((acc, item) => {
      if (item.fullName) {
        acc[item.fullName.trim().toLowerCase()] = item.profileImageName || '';
      }
      return acc;
    }, {} as Record<string, string>);
  }

  buildRequestImageMap(requests: AccountOpeningRequestResponse[]): Record<number, string> {
    return requests.reduce((acc, item) => {
      acc[item.id] = item.applicantImageName || '';
      return acc;
    }, {} as Record<number, string>);
  }

  buildRequestImageByCode(requests: AccountOpeningRequestResponse[]): Record<string, string> {
    return requests.reduce((acc, item) => {
      if (item.customerCode) {
        acc[item.customerCode.trim().toLowerCase()] = item.applicantImageName || '';
      }
      return acc;
    }, {} as Record<string, string>);
  }

  buildRequestImageByName(requests: AccountOpeningRequestResponse[]): Record<string, string> {
    return requests.reduce((acc, item) => {
      if (item.customerName) {
        acc[item.customerName.trim().toLowerCase()] = item.applicantImageName || '';
      }
      return acc;
    }, {} as Record<string, string>);
  }

  resolveAccountImageName(
    account: AccountResponse | null | undefined,
    customerImageMap: Record<number, string>,
    customerImageByCode: Record<string, string>,
    customerImageByName: Record<string, string>,
    requestImageMap: Record<number, string>,
    requestImageByCode: Record<string, string>,
    requestImageByName: Record<string, string>,
    openingRequests: AccountOpeningRequestResponse[]
  ): string {
    if (!account) {
      return '';
    }

    const customerImage = account.customerId ? customerImageMap[account.customerId] : '';
    if (customerImage) {
      return customerImage;
    }

    const customerCodeImage = account.customerCode
      ? customerImageByCode[account.customerCode.trim().toLowerCase()]
      : '';
    if (customerCodeImage) {
      return customerCodeImage;
    }

    const customerNameImage = account.customerName
      ? customerImageByName[account.customerName.trim().toLowerCase()]
      : '';
    if (customerNameImage) {
      return customerNameImage;
    }

    const openingRequestImage = account.openingRequestId ? requestImageMap[account.openingRequestId] : '';
    if (openingRequestImage) {
      return openingRequestImage;
    }

    const requestCodeImage = account.customerCode
      ? requestImageByCode[account.customerCode.trim().toLowerCase()]
      : '';
    if (requestCodeImage) {
      return requestCodeImage;
    }

    const requestNameImage = account.customerName
      ? requestImageByName[account.customerName.trim().toLowerCase()]
      : '';
    if (requestNameImage) {
      return requestNameImage;
    }

    const matchedRequest = openingRequests.find(item =>
      (account.requestNo && item.requestNo === account.requestNo) ||
      (!!account.customerId && !!item.customerId && item.customerId === account.customerId)
    );

    return matchedRequest?.applicantImageName || '';
  }

  resolveOpeningRequestImageName(
    request: AccountOpeningRequestResponse | null | undefined,
    customerImageMap: Record<number, string>,
    customerImageByCode: Record<string, string>,
    customerImageByName: Record<string, string>
  ): string {
    if (!request) {
      return '';
    }

    if (request.applicantImageName) {
      return request.applicantImageName;
    }

    const customerImage = request.customerId ? customerImageMap[request.customerId] : '';
    if (customerImage) {
      return customerImage;
    }

    const customerCodeImage = request.customerCode
      ? customerImageByCode[request.customerCode.trim().toLowerCase()]
      : '';
    if (customerCodeImage) {
      return customerCodeImage;
    }

    const customerNameImage = request.customerName
      ? customerImageByName[request.customerName.trim().toLowerCase()]
      : '';
    if (customerNameImage) {
      return customerNameImage;
    }

    return '';
  }
}
