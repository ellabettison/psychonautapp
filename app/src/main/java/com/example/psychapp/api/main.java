package com.example.psychapp.api;

import com.example.psychapp.api.QueryObjects.SubstanceObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class main {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        APIClient apiClient = new APIClient();
        QueryBuilder queryBuilder = new QueryBuilder();

        ArrayList<SubstanceObject> result = apiClient
                .execute(queryBuilder.queryByName("Cocaine")
                        .withName().withEffects().withRoas().getQuery()).get();

        System.out.print(result.toString());

    }
}
