package com.rvladimir;

import io.micronaut.http.annotation.*;

@Controller("/ttrack-be")
public class TtrackBeController {

    @Get(uri = "/", produces = "text/plain")
    public String index() {
        return "Example Response";
    }
}