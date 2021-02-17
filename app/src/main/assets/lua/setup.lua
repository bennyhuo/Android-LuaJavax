print = function(...)
    logger:debug(tostring(...))
end

function redirect_output(path)
    io.output(path)
end