package com.application.order.list.external;

import com.application.UseCase;
import com.application.order.list.OrderListOutput;
import com.domain.pagination.Pagination;
import com.domain.pagination.SearchQuery;

public abstract class ListOrderUseCaseExternal extends UseCase<SearchQuery, Pagination<OrderListOutput>> {
}
