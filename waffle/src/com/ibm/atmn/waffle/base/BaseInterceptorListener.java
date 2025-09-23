package com.ibm.atmn.waffle.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;
import org.testng.annotations.Test;

public class BaseInterceptorListener implements IMethodInterceptor{
	
	@Override
    public List<IMethodInstance> intercept(List<IMethodInstance> methods, ITestContext context)
    {
		if(context.getSuite().getParameter("intersect_groups") == null || !context.getSuite().getParameter("intersect_groups").equals("true"))
			return methods;
        int methCount = methods.size();
        List<IMethodInstance> result = new ArrayList<IMethodInstance>();

        for (int i = 0; i < methCount; i++)
        {
            IMethodInstance instns = methods.get(i);
            List<String> grps = Arrays.asList(instns.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Test.class).groups());
            List<String> suiteGroups = Arrays.asList(context.getIncludedGroups());
            if (grps.containsAll(suiteGroups))
            {
                result.add(instns);
            }                       
        }                       
        return result;
    }

}
